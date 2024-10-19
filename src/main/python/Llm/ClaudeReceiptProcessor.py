import anthropic
from Llm.LlmBase import LlmBase

class ClaudeReceiptProcessor(LlmBase):

    def process(self,receipt_text):
        response = self.text_request(super().get_process_prompt(receipt_text))
        return response
    
    def categorise(self, items, categorires):
        response =self.text_request(super().get_categorise_prompt(items, categorires))
        return response
    
    def process_composite(self,separator,composite_text):
        return self.text_request(super().get_process_from_composite_prompt(separator,composite_text))

    def text_request(self,prompt):
        client = anthropic.Anthropic()

        message = client.messages.create(
            model="claude-3-5-sonnet-20240620",
            max_tokens=1000,
            temperature=0,
            #system="You are a world-class poet. Respond only with short poems.",
            messages=[
                {
                    "role": "user",
                    "content": [
                        {
                            "type": "text",
                            "text": prompt
                        }
                    ]
                }
            ]
        )
        return super().parse_json(message.content[0].text)