import cv2
import numpy as np
from scipy.ndimage import interpolation as inter
from imutils.perspective import four_point_transform
import imutils

class BaseImageProcessor:

    def __init__(self, args):
        self.args = args

    def rescale_image(self, image):
        return cv2.resize(image, None, fx=1.2, fy=1.2, interpolation=cv2.INTER_CUBIC)

    def get_grayscale(self, image):
        return cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # noise removal
    def remove_noise(self, image):
        return cv2.medianBlur(image,5)
    
    #thresholding
    def thresholding(self, image):
        return cv2.threshold(image, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)[1]

    #dilation
    def dilate(self, image):
        kernel = np.ones((5,5),np.uint8)
        return cv2.dilate(image, kernel, iterations = 1)
        
    #erosion
    def erode(self, image):
        kernel = np.ones((5,5),np.uint8)
        return cv2.erode(image, kernel, iterations = 1)

    #opening - erosion followed by dilation
    def opening(self, image):
        kernel = np.ones((5,5),np.uint8)
        return cv2.morphologyEx(image, cv2.MORPH_OPEN, kernel)

    #canny edge detection
    def canny(self, image):
        return cv2.Canny(image, 100, 200)

    #skew correction
    def deskew(self, image):
        if self.args["debug"] > 0:
            cv2.imshow("image", image)
            cv2.waitKey(0)
        coords = np.column_stack(np.where(image > 0))
        angle = cv2.minAreaRect(coords.copy())[-1]
        if angle < -45:
            angle = -(90 + angle)
        else:
            angle = -angle
        (h, w) = image.shape[:2]
        center = (w // 2, h // 2)
        M = cv2.getRotationMatrix2D(center, angle, 1.0)
        rotated = cv2.warpAffine(image, M, (w, h), flags=cv2.INTER_CUBIC, borderMode=cv2.BORDER_REPLICATE)
        if self.args["debug"] > 0:
            cv2.imshow("rotated", rotated)
            cv2.waitKey(0)
        return rotated

    #template matching
    def match_template(self, image, template):
        return cv2.matchTemplate(image, template, cv2.TM_CCOEFF_NORMED) 

class AdvancedImageProcessor:

    def __init__(self, args):
        self.args = args
        self.base_image_processor = BaseImageProcessor(args)

    """ def remove_noise(self, image):
        image = self.base_image_processor.dilate(image)
        image = self.base_image_processor.erode(image)
        
        image = cv2.threshold(cv2.GaussianBlur(image, (5, 5), 0), 150, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)[1]
        image = cv2.threshold(cv2.bilateralFilter(image, 5, 75, 75), 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)[1]
        image = cv2.adaptiveThreshold(cv2.bilateralFilter(image, 9, 75, 75), 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                cv2.THRESH_BINARY, 31, 2)
        if self.args["debug"] > 0:
            cv2.imshow("adaptiveThreshold", image)
            cv2.waitKey(0)
        return image    """

    """ def remove_shadows(self, image):
        rgb_planes = cv2.split(image)

        result_planes = []
        result_norm_planes = []
        for plane in rgb_planes:
            dilated_image = cv2.dilate(plane, np.ones((7,7), np.uint8))
            bg_image = cv2.medianBlur(dilated_image, 21)
            diff_image = 255 - cv2.absdiff(plane, bg_image)
            norm_image = cv2.normalize(diff_image,None, alpha=0, beta=255, norm_type=cv2.NORM_MINMAX, dtype=cv2.CV_8UC1)
            result_planes.append(diff_image)
            result_norm_planes.append(norm_image)

        result = cv2.merge(result_planes)

        return result

    
    def deskew_image(self, image, delta=1, limit=5):
        def determine_score(arr, angle):
            data = inter.rotate(arr, angle, reshape=False, order=0)
            histogram = np.sum(data, axis=1)
            score = np.sum((histogram[1:] - histogram[:-1]) ** 2)
            return histogram, score

        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        thresh = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)[1] 
        if self.args["debug"] > 0:
            cv2.imshow("gray", gray)
            cv2.imshow("thresh", thresh)
            cv2.waitKey(0)
        scores = []
        angles = np.arange(-limit, limit + delta, delta)
        for angle in angles:
            histogram, score = determine_score(thresh, angle)
            scores.append(score)

        best_angle = angles[scores.index(max(scores))]

        (h, w) = image.shape[:2]
        center = (w // 2, h // 2)
        M = cv2.getRotationMatrix2D(center, best_angle, 1.0)

        rotated = cv2.warpAffine(image, M, (w, h), flags=cv2.INTER_CUBIC, \
                borderMode=cv2.BORDER_REPLICATE)
        if self.args["debug"] > 0:
            cv2.imshow("rotated", rotated)
            cv2.waitKey(0)
        return rotated """
    
    """ def enhance_image(self, image, high_contrast=True, gaussian_blur=True):#, rotate=True): 
        image = self.base_image_processor.rescale_image(image)

        #if rotate:
            #cv2.imwrite(tmp_path, image)
            #rotate_image(tmp_path, tmp_path)
            #image = cv2.imread(tmp_path)

        image = self.deskew_image(image)
        image = self.remove_shadows(image)

        if high_contrast:
            image = self.base_image_processor.get_grayscale(image)

        if gaussian_blur:
            image = self.remove_noise(image)

        return image """
    
    def fourPointTransform(self,original_image, ratio, receiptCnt):
        receipt = four_point_transform(original_image, receiptCnt.reshape(4, 2) * ratio)
        if self.args["debug"] > 0:
            cv2.imshow("Receipt Transform", imutils.resize(receipt, width=500))
            cv2.waitKey(0)

        return receipt
    
    def edgeDetection(self, resized_image):
        # convert the image to grayscale, blur it slightly, and then apply
        # edge detection
        gray = cv2.cvtColor(resized_image, cv2.COLOR_BGR2GRAY)
        blurred = cv2.GaussianBlur(gray, (5, 5,), 0)
        edged = cv2.Canny(blurred, 75, 200)

        if self.args["debug"] > 0:
            cv2.imshow("Input", resized_image)
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
            output = resized_image.copy()
            cv2.drawContours(output, [receiptCnt], -1, (0, 255, 0), 2)
            cv2.imshow("Receipt Outline", output)
            cv2.waitKey(0)

        return receiptCnt
    
    def findReceiptContour(self, cnts,minArea):
        receiptCnt = None
        for c in cnts:
            epsilon = 0.02 * cv2.arcLength(c, True)
            approx = cv2.approxPolyDP(c, epsilon, True)
            cntArea = cv2.contourArea(c)
            if len(approx) == 4 and cntArea > minArea:
                receiptCnt = approx
                break

        return receiptCnt
