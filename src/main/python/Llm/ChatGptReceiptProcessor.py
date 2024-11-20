from openai import NotGiven, OpenAI
from Llm.LlmBase import LlmBase
import os

class ChatGptReceiptProcessor(LlmBase):
    def __init__(self, parse_model):
        super().__init__()
        self.api_key = os.getenv("GPT_API_KEY")
        self.parse_model = parse_model
        self.client = OpenAI(api_key = self.api_key)


    def process(self,receipt_text):
        return self.text_request(super().get_process_prompt(receipt_text))
    
    def categorise(self, items, categories):
        return self.text_request(super().get_categorise_prompt(items,categories))
    
    def ocr_image(self,image):
        return self.image_request(image,super().get_ocr_image_prompt())
    
    def process_from_image(self, image):
        return self.image_request(image,super().get_process_from_image_prompt())    
    
    def process_composite(self,separator,composite_text):
        return self.text_request(super().get_process_from_composite_prompt(separator,composite_text))
        
    def text_request(self,prompt):        
        completion = self.client.chat.completions.create(
            model= self.parse_model, #"gpt-4o-mini",
            messages=[
                {"role": "user", "content": prompt}
            ],
            response_format = { "type": "json_object" }
        )
        reply = completion.choices[0].message.content
        return super().parse_json(reply)
    
    def image_request(self, image, prompt:str):
        image = super().resize_image_if_needed(image)
        base64_image = super().encode_image(image)
        if("json" in prompt.lower()):
            response_format = { "type": "json_object" }
        else:
            response_format = NotGiven()
        completion = self.client.chat.completions.create(
            model= self.parse_model, #"gpt-4o-mini",
            messages=[
                {"role": "user", "content":[
                        {"type": "text", "text": prompt},
                        {
                        "type": "image_url",
                        "image_url": {
                             "url":  f"data:image/jpeg;base64,{base64_image}"
                        },
                    },
                ]}
            ],
            response_format = response_format
        )
        reply = completion.choices[0].message.content
        return super().parse_json(reply)


