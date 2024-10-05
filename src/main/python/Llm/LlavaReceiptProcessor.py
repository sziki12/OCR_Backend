from Llm.LlmBase import LlmBase
import ollama

class LlavaReceiptProcessor(LlmBase):
    def __init__(self) -> None:
        super().__init__()

    def textToResponseJson(self,prompt):

        response = ollama.chat(model='llava:13b', messages=[
        {
            'role': 'user',
            'content': prompt,
        },
        ])
        return response
    
    def imageToResponseJson(self,prompt,image_path):
        base64_image = super().encode_image(image_path)
        response = ollama.chat(model='llava:13b', messages=[
        {
            'role': 'user',
            'content': prompt,
            "stream": False,
            'images': [str(base64_image)]
        },
        ])
        return response

    def process(self,receipt_text):
        response = self.textToResponseJson(super().get_process_prompt(receipt_text))
        reply = response['message']['content']
        return super().parse_json(reply)
    
    def categorise(self, items, categorires):
        response =self.textToResponseJson(super().get_categorise_prompt(items, categorires))
        reply = response['message']['content']
        return super().parse_json(reply)   
    
    def process_from_image(self, image_path):
        response = self.imageToResponseJson(super().get_process_from_image_prompt(),image_path)
        reply = response['message']['content']
        return super().parse_json(reply) 