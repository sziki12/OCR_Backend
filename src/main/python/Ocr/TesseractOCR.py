import cv2
import pytesseract
from paddleocr import PaddleOCR,draw_ocr
from imutils.perspective import four_point_transform
import imutils
import ImageProcessing as ip
from PIL import Image,ImageFont,ImageDraw

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

		def determineRowParams(texts, boxes):
			longest = max(texts, key = len)
			i = texts.index(longest)
			left_top = boxes[i][0]
			right_top = boxes[i][1]
			#right_bot = boxes[i][2]
			left_bot = boxes[i][3]
			return (abs(left_top[0] - right_top[0]), abs(left_top[1] - left_bot[1]))

		# Paddleocr supports Chinese, English, French, German, Korean and Japanese.
		# You can set the parameter `lang` as `ch`, `en`, `fr`, `german`, `korean`, `japan`
		# to switch the language model in order.
		ocr = PaddleOCR(use_angle_cls=True, lang='en', use_gpu=False) #, use_gpu=True   need to run only once to download and load model into memory
		img_path = self.args["path"]+"/"+self.args["image"]
		results = ocr.ocr(img_path, cls=True)

		#image = Image.open(img_path).convert("RGB")
		#draw = ImageDraw.Draw(image)
		#font = ImageFont.load_default()

		result = results[0]
		#image = Image.open(img_path).convert('RGB')
		boxes = [line[0] for line in result]
		sorted_boxes = boxes.copy()
		sorted_boxes.sort()
		texts = [line[1][0] for line in result]
		scores = [line[1][1] for line in result]

		prev_pos = None
		out_text = ""
		row_params = determineRowParams(texts,boxes)
		for i in range(len(texts)):
			index_to_write = boxes.index(sorted_boxes[i])
			print_next_row = True
			print((str(boxes[index_to_write])+" "+str(texts[index_to_write])+" r: "+str(scores[index_to_write])))
			if prev_pos != None:
				left_top = boxes[index_to_write][0]
				#right_top = boxes[index_to_write][1]
				#right_bot = boxes[index_to_write][2]
				#left_bot = boxes[index_to_write][3]
				prev_left_top = prev_pos[0]
				#prev_right_top = prev_pos[1]
				#prev_right_bot = prev_pos[2]
				#prev_left_bot = prev_pos[3]
				if abs(left_top[0] - prev_left_top[0]) <= row_params[0] and abs(left_top[1] - prev_left_top[1]) <= row_params[1]:
					print_next_row = False

			prev_pos = boxes[index_to_write]
			if print_next_row == True:
				out_text += ("\n"+str(texts[index_to_write]))
			else: 
				out_text += (" "+str(texts[index_to_write]))

		return out_text


