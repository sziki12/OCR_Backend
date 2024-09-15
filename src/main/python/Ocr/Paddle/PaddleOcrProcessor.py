from paddleocr import PaddleOCR,draw_ocr
import Ocr.ImageProcessing as ip
import cv2
import imutils
import json
from PIL import Image,ImageFont,ImageDraw
from Ocr.Rect.WordRectangle import WordRectangle
from Ocr.Rect.OcrDocument import OcrDocument
from Ocr.ManualReceiptProcessor import ManualReceiptProcessor
from Ocr.ChatGptReceiptProcessor import ChatGptReceiptProcessor
import Ocr.ImageProcessing as ip
from Ocr.Debug.Debugger import Debugger

class PaddleOcrProcessor:
    def __init__(self,args):
        self.args = args
        self.advanced_image_processor = ip.AdvancedImageProcessor(args)
        self.base_image_processor = self.advanced_image_processor.base_image_processor
        self.debugger = Debugger(args["debug"])

    """ 
    Must have for paddle ocr for consistency, some images (where the fourPointTransform is walid option) are ocr-ed after rotated 270 degres if the fourPointTransform is not called.
    """
    def preprocess_and_load_image(self, image_path):
        original = cv2.imread(image_path)
        resized = imutils.resize(original.copy(), width=1000)

        self.debugger.debug_image("resized", resized)

        cnts = self.advanced_image_processor.edgeDetection(resized)
        ratio = original.shape[1] / float(resized.shape[1])
        processed_image = original.copy()
        if cnts is not None:
            processed_image = self.advanced_image_processor.fourPointTransform(processed_image, ratio, cnts)
        processed_image = self.base_image_processor.deskew(processed_image,"portrait")   
            
        return processed_image
    '''
    Returns the width and height of a bouinding box as touple
    '''
    def determine_row_params(self, texts, boxes):
            longest = max(texts, key = len)
            i = texts.index(longest)
            left_top = boxes[i][0]
            right_top = boxes[i][1]
            #right_bot = boxes[i][2]
            left_bot = boxes[i][3]
            return (abs(left_top[0] - right_top[0]), abs(left_top[1] - left_bot[1]))		

    def save_processed_image(self, image_path, image_name, results):
        image = Image.open(image_path).convert("RGB")
        draw = ImageDraw.Draw(image)
        font = ImageFont.load_default()
        # Process and draw results
        for res in results:
            for line in res:
                box = [tuple(point) for point in line[0]]
                # Finding the bounding box
                box = [(min(point[0] for point in box), min(point[1] for point in box)),
                    (max(point[0] for point in box), max(point[1] for point in box))]
                txt = line[1][0]
                draw.rectangle(box, outline="red", width=2)  # Draw rectangle
                draw.text((box[0][0], box[0][1] - 25), txt, fill="blue", font=font)  # Draw text above the box
        # Save result
        image.save(image_name+"-result.jpg")
		
    def read_receipt_with_paddle(self):
        # Paddleocr supports Chinese, English, French, German, Korean and Japanese.
        # You can set the parameter `lang` as `ch`, `en`, `fr`, `german`, `korean`, `japan`
        # to switch the language model in order.
        ocr = PaddleOCR(use_angle_cls=True, lang='en', use_gpu=False) #, use_gpu=True   need to run only once to download and load model into memory
        image_path = self.args["path"]+"/"+self.args["image"]
        image = self.preprocess_and_load_image(image_path)
        results = ocr.ocr(image, cls=True)

        result = results[0]
        
        boxes = [line[0] for line in result]
        sorted_boxes = boxes.copy()
        sorted_boxes.sort()
        texts = [line[1][0] for line in result]
        scores = [line[1][1] for line in result]

        document = OcrDocument(boxes, texts, scores)
        receipt_text = document.get_text()

        receipt_text_processor =  ChatGptReceiptProcessor(receipt_text, self.args["openai_api_key"])#ManualReceiptProcessor("---","///",0)
        processed_text = receipt_text_processor.process()

        if self.args["debug"] > 0:
            self.save_processed_image(image_path, self.args["image"], results)

        response_json = {
            "processed_receipt": json.loads(processed_text),
            "receipt_text": receipt_text
        }
        return json.dumps(response_json)