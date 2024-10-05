import anthropic
from Llm.LlmBase import LlmBase

class ClaudeReceiptProcessor(LlmBase):

    def process(self,receipt_text):
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
                            "text": super().get_process_prompt(receipt_text)
                        }
                    ]
                }
            ]
        )
        return super().parse_json(message.content[0].text)