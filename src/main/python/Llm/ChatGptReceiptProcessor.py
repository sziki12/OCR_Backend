from openai import OpenAI
from Llm.LlmBase import LlmBase
import os

class ChatGptReceiptProcessor(LlmBase):
    def __init__(self, parse_model):
        super().__init__()
        self.api_key = os.getenv("GPT_API_KEY")
        self.parse_model = parse_model
        self.client = OpenAI(api_key = self.api_key)
        
    def process(self,receiptText):        
        completion = self.client.chat.completions.create(
            model= self.parse_model, #"gpt-4o-mini",
            messages=[
                {"role": "user", "content": super().get_process_prompt(receiptText)}
            ],
            response_format = { "type": "json_object" }
        )
        reply = completion.choices[0].message.content
        return super().parse_json(reply)
    
    def categorise(self, items, categorires):
        completion = self.client.chat.completions.create(
            model= self.parse_model, #"gpt-4o-mini",
            messages=[
                {"role": "user", "content": super().get_categorise_prompt(items, categorires)}
            ],
            response_format = { "type": "json_object" }
        )
        reply = completion.choices[0].message.content
        return super().parse_json(reply)


