from llama_cpp.llama import Llama
import json

class ReceiptLlamaWrapper:
    def __init__(self,pathToModel,role,prompt,temperature):
        self.llm = Llama(
            model_path=pathToModel,
            chat_format="chatml",
            n_gpu_layers=-1, # Uncomment to use GPU acceleration
            # seed=1337, # Uncomment to set a specific seed
            n_ctx=2048, # Uncomment to increase the context window
            verbose=False,
        )
        self.prompt = prompt
        self.role = role
        self.temperature = temperature
        
    
    def textToResponseJson(self,toProcess):
        self.response = self.llm.create_chat_completion(
        messages=[
            {
                "role": "system",
                "content": self.role,
            },
            {"role": "user", "content":  self.prompt+"\n"+toProcess},
        ],
        response_format={
            "type": "json_object",
        },
        temperature=self.temperature,
)
        
    def textToResponseStructuredJson(self,toProcess):
        self.response = self.llm.create_chat_completion(
        messages=[
            {
                "role": "system",
                "content": self.role,
            },
            {"role": "user", "content":  self.prompt+"\n"+toProcess},
        ],
        response_format={
            "type": "json_object",
        "schema": {
            "type": "object",
            "properties": {"response": {"type": "list"}},
            "required": ["response"],
        },
        },
        temperature=self.temperature,
)
    
    
    def printResponse(self):
        print(self.response['choices'][0]['message']['content'])