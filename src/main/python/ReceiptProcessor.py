import numpy as np
import re

import threading

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
    

    def getItems(self,rows):
        items = np.array([])
        row_size = self.getItemRowSize(rows)
        i = 0
        
        for row in rows:
            element =  self.findPatternRow(row,self.getItemPattern())
            if(element is not None):
                for prev in range(i-row_size+1,i):
                    items = np.append(items,rows[prev])
                items = np.append(items,rows[i])
            i+=1
        self.items = items
        self.row_size = row_size
        #print("Items Finished")
        
        

    def process(self,rawReceiptText):
        #print("Processing Started")
        receiptText = self.filterText(rawReceiptText)
        #print("Rows Filtered")
        rows = receiptText.split("\n")
        
        t1 = threading.Thread(target=self.getItems, args=(rows,))
        t2 = threading.Thread(target=self.getDate, args=(rows,))
        #print("Multi Thread Started")
        t1.start()
        t2.start()
        
        t1.join()
        t2.join()
        #print("Finish")
        self.printItems(rawReceiptText,receiptText,self.items,self.row_size,self.date)

    def getItemPattern(self):
        character = '[A-ZOÓÖŐUÚÜŰÍÉÁ:0-9-()]'
        afa = '([A-Z][0-9][0-9]|[A-Z]OO)'
        currency = '(FT|HUF|EUR|\$|\€|{afa})'.format(afa=afa)
        price = '([ ]?([0-9]+[ \.,]?)+)'
        validPrice = '{price}[ ]*{currency}|{currency}[ ]*{price}'.format(price=price,currency=currency)
        name = '(('+character+'+[ ]?)+)'

        pricePattern = '({name}[ \n]*{price})|({price}[ \n]*{name})'.format(name=name,price=validPrice)
        return r''+pricePattern
    
    def getDatePattern(self):
        
        separator = "[\. ,-]"
        date = "([1-9][0-9][0-9][0-9]{s}{s}*[0-9][0-9]{s}{s}*[0-9][0-9])".format(s=separator)
        time = "([ 0-9][0-9][ ]*:[ ]*[0-9][0-9])"
        pattern = "({date}{s}{s}*{time})|({date})".format(date=date,s=separator,time=time)
        return r''+pattern

    #Filters rows that might be too long to be readable
    def lengthFilter(self,row):
        return len(row)<40 and len(row)>0
    
    def findPatternRow(self,row,pattern):
        element = re.search(re.compile(pattern), row.upper())
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
            element = self.findPatternRow(row,self.getItemPattern())
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
    
    def getDate(self,rows):
        self.date = None
        for row in rows:
            element =  self.findPatternRow(row,self.getDatePattern())
            if(element is not None):
                self.date = element.group()
        #print("Date Finished")
    
    def printItems(self,rawReceiptText,filteredReceiptText,items,row_size,date):
        print(self.separator)
        print(rawReceiptText)#rawReceiptText
        print(self.separator)
        print(filteredReceiptText)#filteredReceiptText
        print(self.separator)
        i = 0
        for item in items:
            print(item)
            i+=1
            if(i%row_size == 0 and i != 0):
                print(self.itemSeparator)
        print(self.separator)
        print(date)#date
        
