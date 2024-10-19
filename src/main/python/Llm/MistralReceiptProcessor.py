import os
from mistralai import Mistral
from Llm.LlmBase import LlmBase

class MistralReceiptProcessor(LlmBase):
    def __init__(self) -> None:
        self.api_key = os.getenv("MISTRAL_API_KEY")
        self.client = Mistral(api_key=self.api_key)
        
    def process(self,receipt_text):
        return self.text_request(super().get_process_prompt(receipt_text))
    
    def process_composite(self,separator,composite_text):
        return self.text_request(super().get_process_from_composite_prompt(separator,composite_text))
    
    def categorise(self, items, categories):
        return self.text_request(super().get_categorise_prompt(items,categories))
    
    def ocr_image(self,image_path):
        return self.image_request(image_path,super().get_ocr_image_prompt())
    
    def process_from_image(self, image_path):
        return self.image_request(image_path,super().get_process_from_image_prompt())
    

    def image_request(self,image_path,prompt):
        base64_image = super().encode_image(image_path)
        chat_response = self.client.chat.complete(
            model= "pixtral-12b-2409",
            messages = [
                {
                    "role": "user",
                    "content": [
                        {
                            "type": "text",
                            "text": prompt
                        },
                        {
                            "type": "image_url",
                            "image_url": f"data:image/jpeg;base64,{base64_image}" 
                        }
                    ]
                }
            ]
        )
        reply = chat_response.choices[0].message.content
        return super().parse_json(reply)
    
    def text_request(self,prompt):
        chat_response = self.client.chat.complete(
            model= "mistral-large-latest",
            messages = [
                {
                    "role": "user",
                    "content": prompt,
                }
            ],
        )
        reply = chat_response.choices[0].message.content
        reply = super().parse_json(reply)
        return reply