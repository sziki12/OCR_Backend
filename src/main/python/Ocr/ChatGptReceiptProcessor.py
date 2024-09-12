from openai import OpenAI

class ChatGptReceiptProcessor:
    def __init__(self, receiptText, api_key):
        self.api_key = api_key
        self.prompt = "Extract the store address, store name, total cost, date of purchase and for all purchased items the quantity, name and price from the following hungarian or english receipt and respond in json. If an item's cost is unknown make it 0, if it's quantity is unknown make it 1, if a string is not found make it empty. The text is:"
        self.receiptText = receiptText
        
    def process(self):        
        client = OpenAI(api_key = self.api_key)
        completion = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "user", "content": self.prompt+"\n"+self.receiptText}
            ],
            response_format = { "type": "json_object" }
        )
        reply = completion.choices[0].message.content
        return reply



