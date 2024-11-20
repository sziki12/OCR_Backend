import google.generativeai as genai
import pathlib
from Llm.LlmBase import LlmBase
import os
import uuid
import cv2

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
    
    def ocr_image(self,image):
        return self.image_request(image,super().get_ocr_image_prompt())
    
    def process_from_image(self, image):
        return self.image_request(image, super().get_process_from_image_prompt())
    

    def image_request(self,image,prompt):
        image = super().resize_image_if_needed(image)
        image_name = str(uuid.uuid4())+".jpg"
        cwd = os.getcwd()
        temp_path = os.path.join(cwd,"Temp")
        if not os.path.exists(temp_path):
            os.makedirs(temp_path)
        image_path = os.path.join(temp_path,image_name)    
        cv2.imwrite(image_path,image)
        
        media = pathlib.Path(image_path)
        myfile = genai.upload_file(media)

        #os.remove(image_path)
        
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