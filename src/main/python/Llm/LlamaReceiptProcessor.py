from Llm.LlmBase import LlmBase
import ollama

class LlamaReceiptProcessor(LlmBase):
    def __init__(self, model) -> None:
        super().__init__()
        match model:
            case "llama3.1":
                model_name = 'llama3.1:8b'
        self.model =  model_name       

    def text_request(self,prompt):

        response = ollama.chat(model='llama3.1:8b', messages=[
        {
            'role': 'user',
            'content': prompt,
        },
        ])
        reply = response['message']['content']
        return super().parse_json(reply)

    def process(self,receipt_text):
        response = self.text_request(super().get_process_prompt(receipt_text))
        return response
    
    def categorise(self, items, categorires):
        response =self.text_request(super().get_categorise_prompt(items, categorires))
        return response
    
    def process_composite(self,separator,composite_text):
        return self.text_request(super().get_process_from_composite_prompt(separator,composite_text))