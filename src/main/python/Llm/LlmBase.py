import base64

class LlmBase:
    def __init__(self) -> None:
        pass

    def get_process_prompt(self,receiptText):
        return "Extract the store address, store name as store_name, total cost as total_cost, date of purchase as date_of_purchase in date format and for all purchased items the quantity, name and price in the purchased_items list from the following hungarian or english receipt and respond in json. If an item's cost is unknown make it 0 else return only the number, if it's quantity is unknown make it 1, if a string is not found make it empty. Be cautious the receipt might contain the payment method with the paid money without change The text is:\n"+receiptText
    
    def get_categorise_prompt(self, items, categories):    
        return "Please categorize the following items into their respective categories and respond is json. The keys sould be the categories and the values the list of the associated items. Each item should belong to only one category it is most suitable in. There might be categories without any item. Use only the provided categories and items, which are separated by commas.\nCategories: {categories}\nItems to categorize: {items}".format(categories=categories,items=items)
    
    def get_process_from_image_prompt(self):
        return "Extract the store address, store name as store_name, total cost as total_cost, date of purchase as date_of_purchase in date format and for all purchased items the quantity, name and price in the purchased_items list from the following hungarian or english receipt image as processed_receipt and also return the extracted text as receipt_text. Respond in json. If an item's cost is unknown make it 0 else return only the number, if it's quantity is unknown make it 1, if a string is not found make it empty. Be cautious the receipt might contain the payment method with the paid money without change."
    
    def encode_image(self, image_path):
        """Encode the image to base64."""
        try:
            with open(image_path, "rb") as image_file:
                return base64.b64encode(image_file.read()).decode('utf-8')
        except FileNotFoundError:
            print(f"Error: The file {image_path} was not found.")
            return None
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