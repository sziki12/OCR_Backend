import pytesseract
import cv2
import imutils
import json
from types import SimpleNamespace
import Ocr.ImageProcessing as ip
from Ocr.ManualReceiptProcessor import ManualReceiptProcessor
from Ocr.ChatGptReceiptProcessor import ChatGptReceiptProcessor

class TesseractOcrProcessor:
    def __init__(self,args):
        self.args = args
        self.original = cv2.imread(args["path"]+"/"+args["image"])
        copy = self.original.copy()
        self.resized = imutils.resize(copy, width=1000)
        self.ratio = self.original.shape[1] / float(self.resized.shape[1])
        self.advanced_image_processor = ip.AdvancedImageProcessor(args)

    def preprocess_and_load_image(self, image_path):
        original = cv2.imread(image_path)
        resized = imutils.resize(original.copy(), width=1000)

        self.debugger.debug_image("resized", resized)

        cnts = self.advanced_image_processor.edgeDetection(resized)
        ratio = original.shape[1] / float(resized.shape[1])
        processed_image = original.copy()
        if cnts is not None:
            processed_image = self.advanced_image_processor.fourPointTransform(processed_image, ratio, cnts)
        processed_image = self.base_image_processor.deskew(processed_image, self.args["orientation"])   
            
        return processed_image  
                
    def read_receipt_with_tesseract(self):
        
        receipt = self.get_receipt()

        options = "--psm 4 -l hun+eng"
        receipt_text = pytesseract.image_to_string(
            cv2.cvtColor(receipt, cv2.COLOR_BGR2RGB),
            config=options)
        
        receipt_text_processor =  ChatGptReceiptProcessor(receipt_text, self.args["parse_model"], self.args["openai_api_key"])#ManualReceiptProcessor("---","///",0)
        processed_text = receipt_text_processor.process()
        response_json = {
            "processed_receipt": json.loads(processed_text),
            "receipt_text": receipt_text
        }
        return json.dumps(response_json)

    def get_receipt(self):
        cnts = self.advanced_image_processor.edgeDetection(self.resized)

        if cnts is not None:
            receipt = self.advanced_image_processor.fourPointTransform(self.original, self.ratio, cnts) 
        else:
            receipt = self.original.copy()

        return receipt