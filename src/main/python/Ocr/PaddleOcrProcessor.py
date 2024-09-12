from paddleocr import PaddleOCR,draw_ocr
import Ocr.ImageProcessing as ip
from PIL import Image,ImageFont,ImageDraw
from Ocr.Rect.WordRectangle import WordRectangle
from Ocr.Rect.OcrDocument import OcrDocument
from Ocr.ManualReceiptProcessor import ManualReceiptProcessor
from Ocr.ChatGptReceiptProcessor import ChatGptReceiptProcessor

class PaddleOcrProcessor:
    def __init__(self,args):
        self.args = args

    def determineRowParams(self, texts, boxes):
            longest = max(texts, key = len)
            i = texts.index(longest)
            left_top = boxes[i][0]
            right_top = boxes[i][1]
            #right_bot = boxes[i][2]
            left_bot = boxes[i][3]
            return (abs(left_top[0] - right_top[0]), abs(left_top[1] - left_bot[1]))		

    def showProcessedImage(self, img_path, image_name, results):
        image = Image.open(img_path).convert("RGB")
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
		
    def readReceiptWithPaddle(self):
        # Paddleocr supports Chinese, English, French, German, Korean and Japanese.
        # You can set the parameter `lang` as `ch`, `en`, `fr`, `german`, `korean`, `japan`
        # to switch the language model in order.
        ocr = PaddleOCR(use_angle_cls=True, lang='en', use_gpu=False) #, use_gpu=True   need to run only once to download and load model into memory
        img_path = self.args["path"]+"/"+self.args["image"]
        results = ocr.ocr(img_path, cls=True)

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
       # if self.args["debug"] > 0:
        #    self.showProcessedImage(img_path, self.args["image"], results)
        return processed_text