
import argparse
import re
import numpy as np
from ReceiptProcessor import ReceiptProcessor
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

separator = args["separator"]
itemSeparator = "------"

receiptProcessor = ReceiptProcessor(separator,itemSeparator)
receiptProcessor.process(receiptText)

#Llama prompt: Please extract from the given hungarian receipt the items name, price and quantity in a JSON format:
#ITEMS LIST
#\