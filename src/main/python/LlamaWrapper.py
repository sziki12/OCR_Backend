from llama_cpp.llama import Llama, LlamaGrammar
import httpx
import json
grammar_text = httpx.get("https://raw.githubusercontent.com/ggerganov/llama.cpp/master/grammars/json_arr.gbnf").text
grammar = LlamaGrammar.from_string(grammar_text)


class ReceiptLlamaWrapper:
    def __init__(self):
        self.llm = Llama(
            model_path="model\llama-2-7b-chat.Q5_K_M.gguf",
            chat_format="chatml",
            n_gpu_layers=-1, # Uncomment to use GPU acceleration
            # seed=1337, # Uncomment to set a specific seed
            n_ctx=2048, # Uncomment to increase the context window
        )
    
    def textToReceiptJson(self,receiptText):
        response = self.llm.create_chat_completion(
            messages=[
                {
                    "role": "system",
                    "content": "You are a helpful assistant that outputs the summary of receipts in JSON.",
                },
                {"role": "user", "content": "Tell me  the summary of this receip in JSON: "+str(receiptText)},
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
    


    def schemaTest(self):
        response = self.llm.create_chat_completion(
        messages=[
            {
                "role": "system",
                "content": "You are a helpful assistant that outputs in JSON.",
            },
            {"role": "user", "content": "Who won the world series in 2020"},
            ],
            response_format={
                "type": "json_object",
                "schema": {
                    "type": "object",
                    "properties": {"team_name": {"type": "string"}},
                    "required": ["team_name"],
                },
            },
            temperature=0.7,
        )
        return response
    
    
    def printResponse(self):
        print(json.dumps(json.loads(self.response['choices'][0]['text']), indent=4))