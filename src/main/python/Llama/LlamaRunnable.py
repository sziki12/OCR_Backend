import argparse
from LlamaWrapper import ReceiptLlamaWrapper

ap = argparse.ArgumentParser()
ap.add_argument("-rt", "--receiptText", required=True,
	help="The text of the OCR-ed Receipt")
ap.add_argument("-p", "--pathToModel", required=True,
	help="The Path to the Llama model")


test = "======\nADOSZAM: 11970529-2-44\n1784P-99X-S NOI BLUZ 7 Shi. (C08\n996AO-SEX-S NOI NADRAG gy COU\nZ0373-99X-M/L NOI FELSO 3 930 COU\n=-----=\n996AO-SEX-S NOI NADRAG gy COU\nZ0373-99X-M/L NOI FELSO 3 930 COU\nÖSSZESEN: 1 ) 585 Fi Ft\nBANKKÁRTYA: 20 589 Ft\n=-----=\nVYUGTASZAM: 0950/00045\n2024. 03. 14. he. 18\nNAV ELLENGRZ6 K6D:DF79C\nZP A23401039\n=-----="

#args = vars(ap.parse_args())
#llamaWrapper = ReceiptLlamaWrapper(args["pathToModel"])
#llamaWrapper.textToReceiptJson(args["receiptText"])

llamaWrapper = ReceiptLlamaWrapper("D:\\Llama\\Files\\Models\\llama2_7b\\llama-2-7b-chat.Q6_K.gguf")
llamaWrapper.textToReceiptJson(test)
llamaWrapper.printResponse()

