import google.generativeai as genai
import pathlib
from Llm.LlmBase import LlmBase
import os

class GeminiReceiptProcessor(LlmBase):
    def __init__(self) -> None:
        self.api_key = os.getenv("GEMINI_API_KEY")
        genai.configure(api_key=self.api_key)

    def process(self,receipt_text):
        return self.text_request(super().get_process_prompt(receipt_text))
    
    def process_composite(self,separator,composite_text):
        return self.text_request(super().get_process_from_composite_prompt(separator,composite_text))
    
    def categorise(self, items, categorires):
        return self.text_request(super().get_categorise_prompt(items, categorires))
    
    def ocr_image(self,image_path):
        return self.image_request(image_path,super().get_ocr_image_prompt())
    
    def process_from_image(self, image_path):
        return self.image_request(image_path, super().get_process_from_image_prompt())
    

    def image_request(self,image_path,prompt):
        media = pathlib.Path(image_path)
        myfile = genai.upload_file(media)

        model = genai.GenerativeModel("gemini-1.5-flash")
        result = model.generate_content(
            [myfile, "\n\n", prompt]
        )
        return super().parse_json(result.text)
    
    def text_request(self,prompt):
        model = genai.GenerativeModel("gemini-1.5-flash")
        response = model.generate_content(prompt)
        reply = response.text
        return super().parse_json(reply)