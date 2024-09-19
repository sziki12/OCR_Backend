import numpy as np
from enum import Enum

class WordRelation(Enum):
    RIGHT = 0
    LEFT = 1
    ABOVE = 2
    BELOW = 3
    SAME = 4

class WordRectangle:
    def __init__(self,bounding_box, text, probability):
        self.left_top = bounding_box[0]
        self.right_top = bounding_box[1]
        self.right_bot = bounding_box[2]
        self.left_bot = bounding_box[3]

        self.right_midpoint = tuple(i / 2 for i in tuple(np.add(self.right_top, self.right_bot)))
        self.left_midpoint = tuple(i / 2 for i in  tuple(np.add(self.left_top, self.left_bot)))
        self.middle_point = tuple(i / 2 for i in  tuple(np.add(self.right_midpoint, self.left_midpoint)))

        self.shape = tuple(abs(i) / 2 for i in  tuple(np.subtract(self.right_top, self.left_bot)))
        
        #TODO Fix row detection
        self.x_range =  self.shape[0] * 0.9
        self.y_range =  self.shape[1] * 0.9

        self.text = text
        self.probability = probability

        self.row = None
        self.column = None

    def order(self, other_word):
        if not isinstance(other_word,WordRectangle):
            return None
        
        if(self.middle_point[0] > other_word.middle_point[0]):
            return WordRelation.LEFT
        
        elif(self.middle_point[0] < other_word.middle_point[0]):
            return WordRelation.RIGHT
        
        elif(self.middle_point[0] == other_word.middle_point[0]):
            return WordRelation.SAME

    def is_in_same_line(self, otherRect):
        if not isinstance(otherRect, WordRectangle):
            return False
        
        """ xdiff = (self.right_midpoint[0]- self.left_midpoint[0], otherRect.right_top[0]- otherRect.right_bot[0])
        ydiff = (self.right_midpoint[1]- self.left_midpoint[1], otherRect.right_top[1]- otherRect.right_bot[1])

        def det(a, b):
            return a[0] * b[1] - a[1] * b[0]

        div = det(xdiff, ydiff)
        if div == 0:
            return False

        d = (det(self.right_midpoint, self.left_midpoint), det(otherRect.right_top, otherRect.right_bot))
        x = det(d, xdiff) / div
        y = det(d, ydiff) / div """

        (x,y) = intersection(line(self.right_midpoint, self.left_midpoint),  line(otherRect.right_top, otherRect.right_bot))
#TODO Print all point and assert calculation
        """ print("\n")
        print("Self")
        print("Top: "+str(self.right_top)+" "+str(self.left_top))
        print("Bot: "+str(self.right_bot)+" "+str(self.left_bot))

        print("Other")
        print("Top: "+str(otherRect.right_top)+" "+str(otherRect.left_top))
        print("Bot: "+str(otherRect.right_bot)+" "+str(otherRect.left_bot))

        print("Distance: "+str(abs(otherRect.right_midpoint[1]-y))+" Expected: "+str(otherRect.y_range))
        print("Midpoints: "+str(self.right_midpoint)+" "+str(self.left_midpoint))
        print("Target: "+str(otherRect.right_top)+" "+str(otherRect.right_bot))
        print("x,y: "+"("+str(x)+","+str(y)+")") """
        if abs(otherRect.right_midpoint[1]-y) <= otherRect.y_range:
            return True
        
        return False
    


def line(p1, p2):
    A = (p1[1] - p2[1])
    B = (p2[0] - p1[0])
    C = (p1[0]*p2[1] - p2[0]*p1[1])
    return A, B, -C

def intersection(L1, L2):
    D  = L1[0] * L2[1] - L1[1] * L2[0]
    Dx = L1[2] * L2[1] - L1[1] * L2[2]
    Dy = L1[0] * L2[2] - L1[2] * L2[0]
    if D != 0:
        x = Dx / D
        y = Dy / D
        return x,y
    else:
        return False