import google.generativeai as genai
import pathlib
from Llm.LlmBase import LlmBase
import os

class GeminiReceiptProcessor(LlmBase):
    def __init__(self) -> None:
        self.api_key = os.getenv("GEMINI_API_KEY")
        genai.configure(api_key=self.api_key)
        self.model = genai.GenerativeModel(model_name="gemini-1.5-flash")

    def process(self,receipt_text):
        response = self.model.generate_content(super().get_process_prompt(receipt_text))
        reply = response.text
        return super().parse_json(reply)
    
    def categorise(self, items, categorires):
        response = self.model.generate_content(super().get_categorise_prompt(items, categorires))
        reply = response.text
        return super().parse_json(reply)
    
    def process_from_image(self, image_path):
        media = pathlib.Path(image_path)
        myfile = genai.upload_file(media)

        model = genai.GenerativeModel("gemini-1.5-flash")
        result = model.generate_content(
            [myfile, "\n\n", super().get_process_from_image_prompt()]
        )
        return super().parse_json(result.text)