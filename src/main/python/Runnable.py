
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
args = vars(ap.parse_args())

ocr = ReceiptOCRWrapper(args)

receiptText = ocr.readReceipt()

afa = '([A-Z][0-9][0-9])'
price = '([0-9]+[ ]?)+'
name = '([a-zA-Z]+[ ]?)+'

osszesen = '(.SSZESEN)'
pricePattern = r''+name

#r'('+name+'[ ]*|'+price+'[ ]*|'+afa+'[ ]*)+'


print(receiptText)

print("===========")
rows = receiptText.split("\n")
i = 0
for row in rows:
    print(i)
    element = re.search(pricePattern, row)
    if element is not None:
            print("---------")
            #print(rows[i-1])
            print(element)
            #print(rows[i+1])
            print("---------")
    i+=1
            
print("===========")