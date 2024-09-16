from openai import OpenAI

class ChatGptReceiptProcessor:
    def __init__(self, receiptText, parse_model, api_key):
        self.api_key = api_key
        self.prompt = "Extract the store address, store name as store_name, total cost as total_cost, date of purchase as date_of_purchase in date format and for all purchased items the quantity, name and price in the purchased_items list from the following hungarian or english receipt and respond in json. If an item's cost is unknown make it 0 else return only the number, if it's quantity is unknown make it 1, if a string is not found make it empty. Be cautious the receipt might contain the payment method with the paid money without change The text is:"
        self.receiptText = receiptText
        self.parse_model = parse_model
        
    def process(self):        
        client = OpenAI(api_key = self.api_key)
        completion = client.chat.completions.create(
            model= self.parse_model, #"gpt-4o-mini",
            messages=[
                {"role": "user", "content": self.prompt+"\n"+self.receiptText}
            ],
            response_format = { "type": "json_object" }
        )
        reply = completion.choices[0].message.content
        return reply



