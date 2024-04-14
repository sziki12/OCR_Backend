
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
ap.add_argument("-is", "--itemseparator", default="------",
	help="set the separtator between the extracted items")
args = vars(ap.parse_args())

ocr = ReceiptOCRWrapper(args)

receiptText = ocr.readReceipt()

separator = args["separator"]
itemSeparator = args["itemseparator"]

receiptProcessor = ReceiptProcessor(separator,itemSeparator)
#print(receiptProcessor.getDatePattern())
#print(receiptProcessor.findPatternRow("2024, 02, 29. 12:08",receiptProcessor.getDatePattern()))
receiptProcessor.process(receiptText)
