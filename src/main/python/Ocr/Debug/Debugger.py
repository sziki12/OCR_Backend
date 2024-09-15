import cv2
import imutils

class Debugger:
    def __init__(self, isDebugging):
        self.isDebugging = isDebugging

    def debug_image(self, image_name, image):
        if self.isDebugging > 0:
            cv2.imshow(image_name, imutils.resize(image, width=1000))
            cv2.waitKey(0)

    def debug_image_with_contour(self, image_name, image, contour):
        if self.isDebugging > 0 and contour is not None:
            cv2.drawContours(image, [contour], -1, (0, 255, 0), 2)
            cv2.imshow(image_name, image)
            cv2.waitKey(0)        