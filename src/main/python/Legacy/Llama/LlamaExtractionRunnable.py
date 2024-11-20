import argparse
from LlamaWrapper import ReceiptLlamaWrapper

ap = argparse.ArgumentParser()
ap.add_argument("-rt", "--receiptText", required=True,
	help="The text of the OCR-ed Receipt")
ap.add_argument("-p", "--pathToModel", required=True,
	help="The Path to the Llama model")

prompt = "Please extract from the given receipt the items name as string, cost as Number and quantity as Number in a JSON format:"
role = "You will extract items from Hungarian and English receipts in JSON, in an object there should be an array containing the given items, while providing their name, quantity and cost."
temperature = 0.7

args = vars(ap.parse_args())
llamaWrapper = ReceiptLlamaWrapper(pathToModel=args["pathToModel"],
                                   prompt=prompt,
                                   role=role,
                                   temperature=temperature)
llamaWrapper.textToResponseJson(args["receiptText"])
llamaWrapper.printResponse()

