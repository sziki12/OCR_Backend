
import argparse
from Llama import ReceiptLlamaWrapper
from TesseractOCR import ReceiptOCRWrapper

ap = argparse.ArgumentParser()
ap.add_argument("-i", "--image", required=True,
	help="path to input receipt image")
ap.add_argument("-d", "--debug", type=int, default=-1,
	help="whether or not we are visualizing each step of the pipeline")
args = vars(ap.parse_args())


ocr = ReceiptOCRWrapper(args)

receiptText = ocr.readReceipt()

response = ReceiptLlamaWrapper().textToReceiptJson(receiptText)


print("[INFO] Processed:")
print("==================")
print(response)
print("==================")
print("\n")