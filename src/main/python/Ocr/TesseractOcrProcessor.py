import pytesseract
import cv2
import imutils
import ImageProcessing as ip

class TesseractOcrProcessor:
    def __init__(self,args):
        self.args = args
        self.original = cv2.imread(args["path"]+"/"+args["image"])
        copy = self.original.copy()
        self.resized = imutils.resize(copy, width=1000)
        self.ratio = self.original.shape[1] / float(self.resized.shape[1])
        self.advanced_image_processor = ip.AdvancedImageProcessor(args)
                
    def readReceiptWithTesseract(self):
        
        receipt = self.getReceipt()

        options = "--psm 4 -l hun+eng"
        receiptText = pytesseract.image_to_string(
            cv2.cvtColor(receipt, cv2.COLOR_BGR2RGB),
            config=options)

        #TODO Process Text
        return receiptText

    def getReceipt(self):
        cnts = self.advanced_image_processor.edgeDetection(self.resized)

        if cnts is not None:
            receipt = self.advanced_image_processor.fourPointTransform(self.original, self.ratio, cnts) 
        else:
            receipt = self.original.copy()

        #receipt = ip.AdvancedImageProcessing.enhance_image(receipt)
        #if self.args["debug"] > 0:
        #	cv2.imshow("enhance_image", receipt)
        #	cv2.waitKey(0)	
        return receipt