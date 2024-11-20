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
        def get_x(val):
            return val[0]
        def get_y(val):
            return val[1]
        
        xmin = min(map(get_x,bounding_box))
        ymin = min(map(get_y,bounding_box))
        xmax = max(map(get_x,bounding_box))
        ymax = max(map(get_y,bounding_box))

        self.left_top = [xmin,ymin]
        self.right_top = [xmax,ymin]
        self.right_bot = [xmax,ymax]
        self.left_bot = [xmin,ymax]

        self.right_midpoint = tuple(i / 2 for i in tuple(np.add(self.right_top, self.right_bot)))
        self.left_midpoint = tuple(i / 2 for i in  tuple(np.add(self.left_top, self.left_bot)))
        self.middle_point = tuple(i / 2 for i in  tuple(np.add(self.right_midpoint, self.left_midpoint)))

        self.shape = tuple(abs(i) / 2 for i in  tuple(np.subtract(self.right_top, self.left_bot)))
        
        #Selected based on accuracy heuristics
        #self.x_range =  50
        #self.y_range =  50

        self.x_range =  self.shape[0] * 0.9
        self.y_range =  self.shape[1] * 0.9

        self.text = text
        self.probability = probability

        self.row = None
        self.column = None

    def is_in_same_line(self, otherRect):
        if(self.left_midpoint[0] <= otherRect.left_midpoint[0]):
            (x,y) = intersection(line(self.right_midpoint, self.left_midpoint),  line(otherRect.left_top, otherRect.left_bot))
            """ print("line")
            print((self.right_midpoint, self.left_midpoint))
            print("Left")
            print((otherRect.left_top, otherRect.left_bot))"""
        else:
            (x,y) = intersection(line(self.right_midpoint, self.left_midpoint),  line(otherRect.right_top, otherRect.right_bot))   
            """ print("line")
            print((self.right_midpoint, self.left_midpoint))
            print("Right") 
            print((otherRect.right_top, otherRect.right_bot))"""
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

        """print("Self: "+self.text)
        print("Other: "+otherRect.text)
        print("Distance: "+str(abs(otherRect.right_midpoint[1]-y))+" Expected: "+str(otherRect.y_range))
        print("x,y: "+"("+str(x)+","+str(y)+")")"""
        
        if(self.left_midpoint[0] <= otherRect.left_midpoint[0]):
            #print("midpoint: "+str(otherRect.left_midpoint[1]))
            if abs(otherRect.left_midpoint[1]-y) <= otherRect.y_range:  
                return True
        else:
            #print("midpoint: "+str(otherRect.right_midpoint[1]))
            if abs(otherRect.right_midpoint[1]-y) <= otherRect.y_range:
                return True 
        #print("---")
        return False
    
    def order(self, other_word):
        if not isinstance(other_word,WordRectangle):
            return None
        
        if(self.middle_point[0] > other_word.middle_point[0]):
            return WordRelation.RIGHT
        
        elif(self.middle_point[0] < other_word.middle_point[0]):
            return WordRelation.LEFT
        
        elif(self.middle_point[0] == other_word.middle_point[0]):
            return WordRelation.SAME 

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

    
    