import numpy as np
import re

class ReceiptProcessor:

    def __init__(self,separator,itemSeparator):
        self.pricePattern = self.getItemPattern()
        self.separator = separator
        self.itemSeparator = itemSeparator

    
    def filterText(self,rawReceiptText):
        rows = rawReceiptText.split("\n")
        filteredText = ""
        for row in rows:
            if(self.lengthFilter(row)):
                filteredText += row+"\n"
        return filteredText
        

    def process(self,rawReceiptText):
        receiptText = self.filterText(rawReceiptText)
        rows = receiptText.split("\n")
        items = np.array([])
        row_size = self.getItemRowSize(rows)
        i = 0
        
        for row in rows:
            element = self.findItemRow(row)
            if(element is not None):
                for prev in range(i-row_size+1,i):
                    items = np.append(items,rows[prev])
                items = np.append(items,rows[i])
            i+=1
        self.printItems(rawReceiptText,receiptText,items,row_size)

    def getItemPattern(self):
        character = '[A-ZOÓÖŐUÚÜŰÍÉÁ:0-9-()]'
        afa = '([A-Z][0-9][0-9]|[A-Z]OO)'
        currency = '(FT|HUF|EUR|\$|\€|'+afa+')'
        price = '([ ]?([0-9]+[ .,]?)+)'
        validPrice = price+'[ ]*'+currency+'|'+currency+'[ ]*'+price
        name = '(('+character+'+[ ]?)+)'

        pricePattern = r'('+name+'[ \n]*'+validPrice+')|('+validPrice+'[ \n]*'+name+')'
        return pricePattern

    #Filters rows that might be too long to be readable
    def lengthFilter(self,row):
        return len(row)<40 and len(row)>0
    
    def findItemRow(self,row):
        element = re.search(re.compile(self.pricePattern), row.upper())
        if element is not None and element.group() != "":
            return element
        return None

    def getItemRowSize(self,rows):
        i = 0
        first_match = -1
        last_match = -1
        row_size = -1
        matches = 0
        for row in rows:
            element = self.findItemRow(row)
            if(element is not None):
                matches += 1     
                if(matches > 1):
                    row_size += i-last_match
                    last_match = i
                else:
                    first_match = i
                    last_match = first_match     
            i+=1
        row_size = round(row_size / matches)
        return row_size
    
    def printItems(self,rawReceiptText,filteredReceiptText,items,row_size):
        print(self.separator)
        print(rawReceiptText)
        print(self.separator)
        print(filteredReceiptText)
        print(self.separator)
        i = 0
        for item in items:
            print(item)
            i+=1
            if(i%row_size == 0 and i != 0):
                print(self.itemSeparator)
        
