import cv2
import pytesseract
from paddleocr import PaddleOCR,draw_ocr
from imutils.perspective import four_point_transform
import imutils
import ImageProcessing as ip
from PIL import Image

class ReceiptOCRWrapper:
	def __init__(self,args):
		self.args = args
		self.original = cv2.imread(args["path"]+"/"+args["image"])
		copy = self.original.copy()
		self.resized = imutils.resize(copy, width=1000)
		self.ratio = self.original.shape[1] / float(self.resized.shape[1])


	def getReceipt(self):
		cnts = self.edgeDetection()

		if cnts is not None:
			receipt = self.fourPointTransform(cnts) 
		else:
			receipt = self.original.copy()

		#receipt = ip.AdvancedImageProcessing.enhance_image(receipt)
		#if self.args["debug"] > 0:
		#	cv2.imshow("enhance_image", receipt)
		#	cv2.waitKey(0)	
		return receipt
		


	def edgeDetection(self):
		# convert the image to grayscale, blur it slightly, and then apply
		# edge detection
		gray = cv2.cvtColor(self.resized, cv2.COLOR_BGR2GRAY)
		blurred = cv2.GaussianBlur(gray, (5, 5,), 0)
		edged = cv2.Canny(blurred, 75, 200)

		if self.args["debug"] > 0:
			cv2.imshow("Input", self.resized)
			cv2.imshow("Edged", edged)
			cv2.waitKey(0)
			
		cnts = cv2.findContours(edged.copy(), cv2.RETR_EXTERNAL,
			cv2.CHAIN_APPROX_SIMPLE)
		cnts = imutils.grab_contours(cnts)
		cnts = sorted(cnts, key=cv2.contourArea, reverse=True)
		receiptCnt = None

		maxArea = edged.size
		minArea = maxArea * 0.1

		receiptCnt = self.findReceiptContour(cnts,minArea)

		if self.args["debug"] > 0 and receiptCnt is not None:
			output = self.resized.copy()
			cv2.drawContours(output, [receiptCnt], -1, (0, 255, 0), 2)
			cv2.imshow("Receipt Outline", output)
			cv2.waitKey(0)

		return receiptCnt
	
	def findReceiptContour(self,cnts,minArea):
		receiptCnt = None
		for c in cnts:
			epsilon = 0.02 * cv2.arcLength(c, True)
			approx = cv2.approxPolyDP(c, epsilon, True)

			cntArea = cv2.contourArea(c)

			if len(approx) == 4 and cntArea > minArea:
				receiptCnt = approx
				break

		return receiptCnt

	def fourPointTransform(self,receiptCnt):
		receipt = four_point_transform(self.original, receiptCnt.reshape(4, 2) * self.ratio)
		if self.args["debug"] > 0:
			cv2.imshow("Receipt Transform", imutils.resize(receipt, width=500))
			cv2.waitKey(0)

		return receipt
	
	def readReceiptWithTesseract(self):
		
		receipt = self.getReceipt()

		options = "--psm 4 -l hun+eng"
		receiptText = pytesseract.image_to_string(
			cv2.cvtColor(receipt, cv2.COLOR_BGR2RGB),
			#receipt,
			config=options)
	
		return receiptText
	
	def readReceiptWithPaddle(self):
		# Paddleocr supports Chinese, English, French, German, Korean and Japanese.
		# You can set the parameter `lang` as `ch`, `en`, `fr`, `german`, `korean`, `japan`
		# to switch the language model in order.
		ocr = PaddleOCR(use_angle_cls=True, lang='en', use_gpu=False) #, use_gpu=True   need to run only once to download and load model into memory
		img_path = self.args["path"]+"/"+self.args["image"]
		result = ocr.ocr(img_path, cls=True)
		for idx in range(len(result)):
			res = result[idx]
			for line in res:
				print(line)


		# draw result
		result = result[0]
		image = Image.open(img_path).convert('RGB')
		boxes = [line[0] for line in result]
		txts = [line[1][0] for line in result]
		scores = [line[1][1] for line in result]
		print(txts)
		#im_show = draw_ocr(image, boxes, txts, scores, font_path='./fonts/simfang.ttf')
		#im_show = Image.fromarray(im_show)
		#im_show.save('result.jpg')


