import pytesseract
import cv2
import imutils
import Ocr.ImageProcessing as ip
from Ocr.Debug.Debugger import Debugger

class TesseractOcrProcessor:
    def __init__(self,args):
        self.advanced_image_processor = ip.AdvancedImageProcessor(args)
        self.debugger = Debugger(args["debug"])

    def preprocess_and_load_image(self, image):
        #image = self.advanced_image_processor.base_image_processor.thresholding(image)
        resized = imutils.resize(image.copy(), width=3000)
        
        self.debugger.debug_image("resized", resized)

        """ cnts = self.advanced_image_processor.edgeDetection(resized)
        ratio = original.shape[1] / float(resized.shape[1])
        processed_image = original.copy()
        if cnts is not None:
            processed_image = self.advanced_image_processor.fourPointTransform(processed_image, ratio, cnts)
        processed_image = self.base_image_processor.deskewByText(processed_image, self.args["orientation"])   """
            
        return resized #processed_image  
                
    def read_receipt_with_tesseract(self,image):
        
        receipt = self.get_receipt(image)

        options = "--psm 4 -l hun+eng"
        receipt_text = pytesseract.image_to_string(
            cv2.cvtColor(receipt, cv2.COLOR_BGR2RGB),
            config=options)
        
        return receipt_text

    def get_receipt(self,image):
        resized = self.preprocess_and_load_image(image)
        return resized