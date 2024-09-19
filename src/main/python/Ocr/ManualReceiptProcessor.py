import numpy as np
import re
from difflib import get_close_matches

import threading

class ManualReceiptProcessor:

    def __init__(self,separator,itemSeparator,debug):
        self.separator = separator
        self.itemSeparator = itemSeparator
        self.debug = debug

        self.character_pattern = '[A-ZOÓÖŐUÚÜŰÍÉÁ:0-9-()]'
        self.afa_pattern = '([A-Z][0-9O][0-9O])'
        self.currency_pattern = '(FT|HUF|EUR|\$|\€)'
        self.quantity_pattern = '([0-9]+[\.,]?[0-9]*DB)'
        self.price_pattern = '([ ]?([0-9]+[ \.,]?)+)'

        self.keywords = ["ÖSSZESEN", "ADÓSZÁM", "PÉNZTÁROS", "BANK", "NYUGTASZÁM", "NAV ELLENÖRZŐ KÓD","REF.NR"]
        self.found_keywords = {}




    def fuzzy_find_keyword_row(self, rawReceiptText, keyword, accuracy = 0.6):
        rows = rawReceiptText.split("\n")
        for row in rows:
            words = row.split()
            # Get the single best match in the line
            matches = get_close_matches(keyword, words, 1, accuracy)
            if matches:
                return row    

    
    def filter_text(self,rawReceiptText):
        rows = rawReceiptText.split("\n")
        filteredText = ""
        for row in rows:
            if(self.length_filter(row)):
                filteredText += row+"\n"
        return filteredText
    

    def find_items(self, upper_case_receipt_text):
        def flush_current_item(found_price, found_quantity):
            pass
        rows = upper_case_receipt_text.split("\n")
        items = []
        found_price = None
        found_quantity = None
        for row in rows:
            quantity = self.find_pattern_in_row(row, self.quantity_pattern)
            price = self.find_pattern_in_row(row, self.price_pattern)
            if(found_price is None):
                pass #TODO      

    """ def get_items(self,rows):
        items = np.array([])
        row_size = self.get_item_row_size(rows)
        i = 0
        
        for row in rows:
            element =  self.find_pattern_row(row,self.get_item_pattern())
            if(element is not None):
                for prev in range(i-row_size+1,i):
                    items = np.append(items,rows[prev])
                items = np.append(items,rows[i])
            i+=1
        self.items = items
        self.row_size = row_size
        if(self.debug):
            print("Items Finished") """
        
        

    def process(self,rawReceiptText):
        if(self.debug):
            print("Processing Started")
        receiptText = receiptText.upper()
        receiptText = self.filter_text(rawReceiptText)
        if(self.debug):
            print("Rows Filtered")
        rows = receiptText.split("\n")
        
        t1 = threading.Thread(target=self.get_items, args=(rows,))
        t2 = threading.Thread(target=self.get_date, args=(rows,))
        if(self.debug):
            print("Multi Thread Started")
        t1.start()
        t2.start()
        
        t1.join()
        t2.join()
        if(self.debug):
            print("Finish")
        self.print_items(rawReceiptText,receiptText,self.items,self.row_size,self.date)
    
    def get_date_pattern(self):
        
        separator = "[\. ,-]"
        date = "([1-9][0-9][0-9][0-9]{s}{s}*[0-9][0-9]{s}{s}*[0-9][0-9])".format(s=separator)
        time = "([ 0-9][0-9][ ]*:[ ]*[0-9][0-9])"
        pattern = "({date}{s}{s}*{time})|({date})".format(date=date,s=separator,time=time)
        return r''+pattern

    #Filters rows that might be too long to be readable
    def length_filter(self,row):
        return len(row)<40 and len(row)>0
    
    def find_pattern_in_row(self,row,pattern):
        element = re.search(re.compile(pattern), row)
        if element is not None and element.group() != "":
            return element
        return None

    """ def get_item_row_size(self,rows):
        i = 0
        first_match = -1
        last_match = -1
        row_size = -1
        matches = 0
        for row in rows:
            element = self.find_pattern_row(row,self.get_item_pattern())
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
        if(self.debug):
            print("Row Size {size}".format(size=row_size))
        return row_size """
    
    def get_date(self,rows):
        self.date = None
        for row in rows:
            element =  self.find_pattern_row(row,self.get_date_pattern())
            if(element is not None):
                self.date = element.group()
        if(self.debug):
            print("Date Finished:  {date}".format(date=self.date))
    
    def print_items(self,rawReceiptText,filteredReceiptText,items,row_size,date):
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
        
