
import argparse
import re
from TesseractOCR import ReceiptOCRWrapper

ap = argparse.ArgumentParser()
ap.add_argument("-i", "--image", required=True,
	help="image name or path")
ap.add_argument("-p", "--path", required=True,
	help="path to image folder")
ap.add_argument("-d", "--debug", type=int, default=-1,
	help="whether or not we are visualizing each step of the pipeline")
ap.add_argument("-s", "--separator", default="======",
	help="set the separtator between the plain OCR-ed text and the processed text")
args = vars(ap.parse_args())

ocr = ReceiptOCRWrapper(args)

receiptText = ocr.readReceipt()

character = '[A-ZOÓÖŐUÚÜŰÍÉÁ:0-9-()]'

afa = '([A-Z][0-9][0-9]|[A-Z]OO)'
currency = '(FT|HUF|EUR|\$|\€|'+afa+')'
price = '([ ]?([0-9]+[ .,]?)+)'
validPrice = price+'[ ]*'+currency+'|'+currency+'[ ]*'+price


name = '(('+character+'+[ ]?)+)'

pricePattern = r'('+name+'[ \n]*'+validPrice+')|('+validPrice+'[ \n]*'+name+')'

#r'('+name+'[ ]*|'+price+'[ ]*|'+afa+'[ ]*)+'

separator = args["separator"]
itemSeparator = "------"

print(separator)

print(receiptText)

print(separator)
rows = receiptText.split("\n")
i = 0
for row in rows:
    #print(i)
    if(len(row)<40):
        element = re.search(re.compile(pricePattern), row.upper())
        if element is not None and element.group() != "":
                #print(row)
                print(element.group())
                print(itemSeparator)
                
    i+=1
            
    
    # Provide the name, and price of the item from a receipt: VK296-00P-ONE PAPTRTASKA 80 COO
    #in JSON