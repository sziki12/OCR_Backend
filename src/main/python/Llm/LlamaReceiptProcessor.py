from Llm.LlmBase import LlmBase
import ollama

class LlamaReceiptProcessor(LlmBase):
    def __init__(self, model) -> None:
        super().__init__()
        match model:
            case "llama3.1":
                model_name = 'llama3.1:8b'
        self.model =  model_name       

    def textToResponseJson(self,prompt):

        response = ollama.chat(model='llama3.1:8b', messages=[
        {
            'role': 'user',
            'content': prompt,
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