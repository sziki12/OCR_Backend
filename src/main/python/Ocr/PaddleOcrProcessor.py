from paddleocr import PaddleOCR,draw_ocr
import ImageProcessing as ip
from PIL import Image,ImageFont,ImageDraw

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

        prev_pos = None
        out_text = ""
        row_params = self.determineRowParams(texts,boxes)
        for i in range(len(texts)):
            index_to_write = boxes.index(sorted_boxes[i])
            print_next_row = True
            print((str(boxes[index_to_write])+" "+str(texts[index_to_write])+" r: "+str(scores[index_to_write])))
            if prev_pos != None:
                left_top = boxes[index_to_write][0]
                #right_top = boxes[index_to_write][1]
                #right_bot = boxes[index_to_write][2]
                #left_bot = boxes[index_to_write][3]
                prev_left_top = prev_pos[0]
                #prev_right_top = prev_pos[1]
                #prev_right_bot = prev_pos[2]
                #prev_left_bot = prev_pos[3]
                if abs(left_top[0] - prev_left_top[0]) <= row_params[0] and abs(left_top[1] - prev_left_top[1]) <= row_params[1]:
                    print_next_row = False

            prev_pos = boxes[index_to_write]
            if print_next_row == True:
                out_text += ("\n"+str(texts[index_to_write]))
            else: 
                out_text += (" "+str(texts[index_to_write]))

        self.showProcessedImage(img_path, self.args["image"], results)
        return out_text