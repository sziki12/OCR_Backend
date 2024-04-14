from llama_cpp.llama import Llama
import json

class ReceiptLlamaWrapper:
    def __init__(self,pathToModel):
        self.llm = Llama(
            model_path=pathToModel,
            chat_format="chatml",
            n_gpu_layers=-1, # Uncomment to use GPU acceleration
            # seed=1337, # Uncomment to set a specific seed
            n_ctx=2048, # Uncomment to increase the context window
        )
        self.prompt = "Please extract from the given receipt the items name as string, cost as Number and quantity as Number in a JSON format:"
        self.role = "You will extract items from Hungarian and English receipts in JSON, in an object there should be an array containing the given items, while providing their name, quantity and cost."
    
    def textToReceiptJson(self,receiptText):
        self.response = self.llm.create_chat_completion(
        messages=[
            {
                "role": "system",
                "content": self.role,
            },
            {"role": "user", "content":  self.prompt+"\n"+receiptText},
        ],
        response_format={
            "type": "json_object",
        },
        temperature=0.7,
)
    
    
    def printResponse(self):
        print(json.dumps(json.loads(self.response['choices'][0]['text']), indent=4))