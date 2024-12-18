import cv2
import numpy as np
from scipy.ndimage import interpolation as inter
from imutils.perspective import four_point_transform
import imutils
from Ocr.Debug.Debugger import Debugger

class BaseImageProcessor:

    def __init__(self, args):
        self.args = args
        self.debugger = Debugger(args["debug"])

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
    #TODO Fix deskew
    def deskewByLine(self, image,orientation, max_skew=10):
        (height, width) = image.shape[:2]

        # Create a grayscale image and denoise it
        image_grey = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        image_grey = cv2.fastNlMeansDenoising(image_grey, h=3)

        # Create an inverted B&W copy using Otsu (automatic) thresholding
        im_bw = cv2.threshold(image_grey, 0, 255, cv2.THRESH_BINARY_INV | cv2.THRESH_OTSU)[1]

        # Detect lines in this image. Parameters here mostly arrived at by trial and error.
        lines = cv2.HoughLinesP(
            im_bw, 1, np.pi / 180, 200, minLineLength=width / 12, maxLineGap=width / 150
        )
        # Collect the angles of these lines (in radians)
        angles = []
        for line in lines:
            x1, y1, x2, y2 = line[0]
            angles.append(np.arctan2(y2 - y1, x2 - x1))

        # If the majority of our lines are vertical, this is probably a landscape image
        #landscape = np.sum([abs(angle) > np.pi / 4 for angle in angles]) > 7 * len(angles) / 12
        # Filter the angles to remove outliers based on max_skew
        if orientation == "landscape":
            angles = [
                angle
                for angle in angles
                if np.deg2rad(90 - max_skew) < abs(angle) < np.deg2rad(90 + max_skew)
            ]
        elif orientation == "portrait":
            angles = [angle for angle in angles if abs(angle) < np.deg2rad(max_skew)]
        else:
            return image
            
        if len(angles) < 5:
            # Insufficient data to deskew
            return image
        # Average the angles to a degree offset
        angle_deg = np.rad2deg(np.median(angles))
        # If this is landscape image, rotate the entire canvas appropriately
        if orientation == "landscape":
            if angle_deg < 0:
                image = cv2.rotate(image, cv2.ROTATE_90_CLOCKWISE)
                angle_deg += 90
            elif angle_deg > 0:
                image = cv2.rotate(image, cv2.ROTATE_90_COUNTERCLOCKWISE)
                angle_deg -= 90
        # Rotate the image by the residual offset
        M = cv2.getRotationMatrix2D((width / 2, height / 2), angle_deg, 1)
        image = cv2.warpAffine(image, M, (width, height), borderMode=cv2.BORDER_REPLICATE)

        self.debugger.debug_image("Rotated",image)
        return image


    def deskewByText(self,image,misc):
        gray = self.get_grayscale(image)
        gray = cv2.GaussianBlur(gray, (9, 9), 0)

        self.debugger.debug_image("gray",gray)

        resized_height = 480
        percent = resized_height / len(image)
        resized_width = int(percent * len(image[0]))
        gray = cv2.resize(gray,(resized_width,resized_height))

        gray = cv2.bitwise_not(gray)
        thresh = self.thresholding(gray)
        kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (30, 5))
        dilate = cv2.dilate(thresh, kernel)
        
        contours, hierarchy = cv2.findContours(dilate, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)

        angles = []
        for contour in contours:
            minAreaRect = cv2.minAreaRect(contour)
            angle = minAreaRect[-1]
            if angle != 90.0 and angle != -0.0: #filter out 0 and 90
                angles.append(angle)

        angles.sort()
        mid_angle = angles[int(len(angles)/2)]    


        if mid_angle > 45: #anti-clockwise
            mid_angle = -(90 - mid_angle)
        height = image.shape[0]
        width = image.shape[1]
        m = cv2.getRotationMatrix2D((width / 2, height / 2), mid_angle, 1)
        deskewed = cv2.warpAffine(image, m, (width, height), borderValue=(255,255,255))

        self.debugger.debug_image("inverse gray",gray)
        self.debugger.debug_image("bitwise_not",gray)
        self.debugger.debug_image("thresh",thresh)
        self.debugger.debug_image("dilate",dilate)
        self.debugger.debug_image("deskewed",deskewed)
        return deskewed

    #template matching
    def match_template(self, image, template):
        return cv2.matchTemplate(image, template, cv2.TM_CCOEFF_NORMED) 

class AdvancedImageProcessor:

    def __init__(self, args):
        self.args = args
        self.base_image_processor = BaseImageProcessor(args)
        self.debugger = Debugger(args["debug"])

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

        self.debugger.debug_image("Four Point Transform", receipt)
        return receipt
    
    def edgeDetection(self, resized_image):
        # convert the image to grayscale, blur it slightly, and then apply
        # edge detection
        gray = cv2.cvtColor(resized_image, cv2.COLOR_BGR2GRAY)
        blurred = cv2.GaussianBlur(gray, (5, 5,), 0)
        edged = cv2.Canny(blurred, 75, 200)

        self.debugger.debug_image(" edgeDetectionInput", resized_image)
        self.debugger.debug_image(" edgeDetection Edged", edged)
            
        cnts = cv2.findContours(edged.copy(), cv2.RETR_EXTERNAL,
            cv2.CHAIN_APPROX_SIMPLE)
        cnts = imutils.grab_contours(cnts)
        cnts = sorted(cnts, key=cv2.contourArea, reverse=True)
        receiptCnt = None

        maxArea = edged.size
        minArea = maxArea * 0.1

        receiptCnt = self.findReceiptContour(cnts,minArea)

        self.debugger.debug_image_with_contour("Receipt Contour", resized_image.copy(), receiptCnt)

        return receiptCnt
    
    def findReceiptContour(self, cnts,minArea):
        receiptCnt = None
        for c in cnts:
            epsilon = 0.05 * cv2.arcLength(c, True)
            approx = cv2.approxPolyDP(c, epsilon, True)
            cntArea = cv2.contourArea(c)
            if len(approx) == 4 and cntArea > minArea:
                receiptCnt = approx
                break

        return receiptCnt
