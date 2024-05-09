import argparse
from LlamaWrapper import ReceiptLlamaWrapper

ap = argparse.ArgumentParser()
ap.add_argument("-c", "--categories", required=True,
	help="The Categories the model can use to categorise th Items")
ap.add_argument("-i", "--items", required=True,
	help="The Items to be Categorised")
ap.add_argument("-p", "--pathToModel", required=True,
	help="The Path to the Llama model")

args = vars(ap.parse_args())

categories = args["categories"]
items = args["items"]
prompt = "Please categorize the following items into their respective categories. Each item should belong to only one category it is most suitable in. There might be categories without any item. Use only the provided categories and items, which are separated by commas.\nCategories: {categories}\nItems to categorize: {items}".format(categories=categories,items=items)
role = "Your task is to categorize items from both Hungarian and English receipts into predefined expense categories."
temperature = 0.8

#print(prompt)

llamaWrapper = ReceiptLlamaWrapper(pathToModel=args["pathToModel"],
                                   role=role,
                                   prompt=prompt,
                                   temperature=temperature)
llamaWrapper.textToResponseStructuredJson("")
llamaWrapper.printResponse()

