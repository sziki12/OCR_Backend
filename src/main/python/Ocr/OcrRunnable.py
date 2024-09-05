
import argparse
from ReceiptProcessor import ReceiptProcessor
from TesseractOCR import ReceiptOCRWrapper
from openai import OpenAI

ap = argparse.ArgumentParser()

ap.add_argument("-i", "--image", required=True,
	help="image name or path")
ap.add_argument("-p", "--path", required=True,
	help="path to image folder")

ap.add_argument("-ot", "--ocr_type", required=True,
	help="tesseract, paddle")
ap.add_argument("-pt", "--processing_type",
	help="manual, chatgpt")

ap.add_argument("-d", "--debug", type=int, default=-1,
	help="whether or not we are visualizing each step of the pipeline")
ap.add_argument("-s", "--separator", default="======",
	help="set the separtator between the plain OCR-ed text and the processed text")
ap.add_argument("-is", "--itemseparator", default="------",
	help="set the separtator between the extracted items")

args = vars(ap.parse_args())

ocr = ReceiptOCRWrapper(args)
ocr_type = args["ocr_type"]

receiptText = ""

if ocr_type == "tesseract":
    receiptText = ocr.readReceiptWithTesseract()   
elif ocr_type == "paddle":	
	receiptText = ocr.readReceiptWithPaddle()

separator = args["separator"]
itemSeparator = args["itemseparator"]
debug = args["debug"]
print(receiptText)


#prompt = "Extract the store address, total cost, date of purchase and for all purchased items the quantity, name and price from the following hungarian or english receipt:"

#client = OpenAI()
#completion = client.chat.completions.create(
#    model="gpt-4o",
#    messages=[
#        {"role": "user", "content": "Extract the useful informations from the following hungarian or english receipt:"}
#    ]
#)

#receiptProcessor = ReceiptProcessor(separator,itemSeparator,debug)
#receiptProcessor.process(receiptText)