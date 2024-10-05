import os
from mistralai import Mistral
from Llm.LlmBase import LlmBase

class MistralReceiptProcessor(LlmBase):
    def __init__(self, api_key) -> None:
        self.api_key = api_key
        self.client = Mistral(api_key=self.api_key)
    def process(self,receipt_text):
        chat_response = self.client.chat.complete(
            model= "mistral-large-latest",
            messages = [
                {
                    "role": "user",
                    "content": super().get_process_prompt(receipt_text),
                }
            ]
        )
        reply = chat_response.choices[0].message.content
        reply = super().parse_json(reply)
        return reply
    
    def categorise(self, items, categories):
        chat_response = self.client.chat.complete(
            model= "mistral-large-latest",
            messages = [
                {
                    "role": "user",
                    "content": super().get_categorise_prompt(items, categories),
                }
            ],
        )
        reply = chat_response.choices[0].message.content
        reply = super().parse_json(reply)
        return reply
    
    def process_from_image(self, image_path):
        base64_image = super().encode_image(image_path)
        chat_response = self.client.chat.complete(
            model= "pixtral-12b-2409",
            messages = [
                {
                    "role": "user",
                    "content": [
                        {
                            "type": "text",
                            "text": super().get_process_from_image_prompt()
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