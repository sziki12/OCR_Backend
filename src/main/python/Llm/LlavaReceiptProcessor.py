from Llm.LlmBase import LlmBase
import ollama

class LlavaReceiptProcessor(LlmBase):
    def __init__(self) -> None:
        super().__init__()

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
    
    def text_request(self,prompt):

        response = ollama.chat(model='llava:13b', messages=[
        {
            'role': 'user',
            'content': prompt,
        },
        ])
        reply = response['message']['content']
        return super().parse_json(reply)
    
    def image_request(self,image_path,prompt):
        base64_image = super().encode_image(image_path)
        response = ollama.chat(model='llava:13b', messages=[
        {
            'role': 'user',
            'content': prompt,
            "stream": False,
            'images': [str(base64_image)]
        },
        ])
        reply = response['message']['content']
        return super().parse_json(reply)