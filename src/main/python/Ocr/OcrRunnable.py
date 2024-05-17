
import argparse
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
debug = args["debug"]

receiptProcessor = ReceiptProcessor(separator,itemSeparator,debug)
receiptProcessor.process(receiptText)
