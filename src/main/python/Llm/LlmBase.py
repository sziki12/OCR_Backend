import base64
import os
import cv2

class LlmBase:
    def __init__(self) -> None:
        pass

    def get_process_prompt(self,receiptText):
        return self.process_sturcture()+" "+self.text_correction_prompt()+" "+self.process_receipt_base()+" "+self.languages_and_document_type()+"."+self.process_receipt_constraints()+"  The text is:\n"+receiptText
    
    def get_process_from_image_prompt(self):
        return self.process_from_image_sturcture()+" "+self.text_correction_prompt()+" "+self.process_receipt_base()+" "+self.languages_and_document_type()+" image as processed_receipt. Also return the extracted text as receipt_text. "+self.process_receipt_constraints() 
    
    def get_ocr_image_prompt(self):
        return self.extract_sturcture()+" "+self.text_correction_prompt()+" "+"Please extract the text "+self.languages_and_document_type()+" image as receipt_text."
    
    def get_process_from_composite_prompt(self,separator,receipt_texts):
        return self.process_from_image_sturcture()+" "+self.text_correction_prompt()+" "+"You will reive a receipt extracted by multiple Ocr model separated by "+ separator +" characters. You have to combine these to recreate the original receipt, return it as receipt_text. "+self.process_receipt_base()+" "+self.languages_and_document_type()+" as processed_receipt. "+self.process_receipt_constraints()+" The texts are:\n"+receipt_texts
    
    def get_categorise_prompt(self, items, categories):    
        return "Please categorize the following items into their respective categories and respond is json. The keys sould be the categories and the values the list of the associated items. Each item should belong to only one category it is most suitable in. There might be categories without any item. Use only the provided categories and items, which are separated by commas.\nCategories: {categories}\nItems to categorize: {items}".format(categories=categories,items=items)

    def encode_image_from_path(self, image_path:str):
        """Encode the image to base64 by path."""
        try:
            with open(image_path, "rb") as image_file:
                return base64.b64encode(image_file.read()).decode('utf-8')
        except FileNotFoundError:
            print(f"Error: The file {image_path} was not found.")
            return None
        except Exception as e:  # Added general exception handling
            print(f"Error: {e}")
            return None
        
    def encode_image(self, image):
        """Encode the image to base64."""
        try:
            return base64.b64encode(cv2.imencode('.jpg', image)[1]).decode('utf-8')
        except Exception as e:  # Added general exception handling
            print(f"Error: {e}")
            return None
        
    def parse_json(self, response):
        if("```json" in response):
            json = response.split("```json")[1]
            json = json.split("```")[0]
        elif("```" in response):
            json = response.split("```")[1]
        else:
            json = response 

        print("_____")
        print(json)
        print("_____")       
        return json
    
    def resize_image_if_needed(self,image):
        ideal_size = 2000
        h, w = image.shape[:2]
        h_ratio = w/h
        w_ratio = h/w
        if(h > ideal_size and h_ratio <= 1):
            image = cv2.resize(image, (round(ideal_size*h_ratio), ideal_size))
            
        elif(w > ideal_size and w_ratio <= 1):
            image = cv2.resize(image, (ideal_size, round(ideal_size*w_ratio)))
        return image

    def process_receipt_base(self):
        return "Extract the store address, store name as store_name, total cost as total_cost, date of purchase as date_of_purchase in date format and for all purchased items the quantity, name and price per item as price_per_item and total price as total_price in the purchased_items list" 

    def languages_and_document_type(self):
        return "from the following hungarian or english receipt"
    def process_receipt_constraints(self):
        return "If an item's cost is unknown make it 0 else return only the number. If it's quantity is unknown make it 1. If a string can be corrected do it. If a string is not found make it empty. Be cautious the receipt might contain the payment method with the paid money without change." 

    def process_sturcture(self):
        return "Respond in json with an object that has a property named processed_receipt."
    
    def extract_sturcture(self):
        return "Respond in json with an object that has a property named receipt_text."
    
    def process_from_image_sturcture(self):
        return "Respond in json with an object that has a property named receipt_text and processed_receipt."
    
    def text_correction_prompt(self):
        return "Please correct the incorrectly spelled words in all returned text while being cautious of the context."
    

        """You will reive a receipt extracted by multiple Ocr model separated by --- characters. 
        You have to combine these to recreate the original receipt and extract the store address, store name as store_name, total cost as total_cost, date of purchase as date_of_purchase in date format and for all purchased items the quantity, name and price in the purchased_items list from the following hungarian or english receipt and respond in json. 
        If an item's cost is unknown make it 0 else return only the number, if it's quantity is unknown make it 1, if a string is not found make it empty. 
        Be cautious the receipt might contain the payment method with the paid money without change. The texts are:"""