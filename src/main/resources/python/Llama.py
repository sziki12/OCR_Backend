from llama_cpp import Llama


class ReceiptLlamaWrapper:
    def __init__(self):
        self.llm = Llama(
            model_path="model/llama-2-7b-chat.Q5_K_M.gguf",
            chat_format="chatml",
            #n_gpu_layers=-1, # Uncomment to use GPU acceleration
            # seed=1337, # Uncomment to set a specific seed
             #n_ctx=2048, # Uncomment to increase the context window
        )
    
    def textToReceiptJson(self,receiptText):
        response = self.llm.create_chat_completion(
            messages=[
                {
                    "role": "system",
                    "content": "You are a helpful assistant that outputs the sum up of receipts in JSON.",
                },
                {"role": "user", "content": "Sum up this receip in JSON: "+receiptText},
            ],
            response_format={
                "type": "json_object",
                "schema": {
                    "type": "object",
                    "properties": {
                        "items": {
                            "type": "array",
                            "items": {
                                "type": "object",
                                "properties": {
                                    "name":{
                                        "type": "string"
                                    },
                                    "quantity":{
                                        "type": "number"
                                    },
                                    "totalCost":{
                                        "type": "number"
                                    },
                                    "required": ["name", "quantity", "totalCost"],
                                }
                            }
                        },
                        "dateOfPurchase":{
                            "type": "string"
                        },
                        "description":{
                            "type": "string"
                        },
                        "totalCost":{
                            "type": "number"
                        }
                    },
                    "required": ["totalCost", "items", "dateOfPurchase"],
                },
            },
            temperature=0.7,
        )

        return response