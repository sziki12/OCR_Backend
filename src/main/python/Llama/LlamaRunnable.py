import argparse
from LlamaWrapper import ReceiptLlamaWrapper

ap = argparse.ArgumentParser()
ap.add_argument("-rt", "--receiptText", required=True,
	help="The text of the OCR-ed Receipt")
ap.add_argument("-p", "--pathToModel", required=True,
	help="The Path to the Llama model")

args = vars(ap.parse_args())
llamaWrapper = ReceiptLlamaWrapper(args["pathToModel"])
llamaWrapper.textToReceiptJson(args["receiptText"])
llamaWrapper.printResponse()

