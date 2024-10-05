from transformers import T5Tokenizer, T5ForConditionalGeneration, T5Model
from Llm.LlmBase import LlmBase

class T5ReceiptProcessor(LlmBase):

    def process(self,receipt_text):
        tokenizer = T5Tokenizer.from_pretrained("t5-base")
        model = T5Model.from_pretrained("t5-base")

        input_ids = tokenizer(super().get_process_prompt(receipt_text), return_tensors="pt").input_ids
        outputs = model.generate(input_ids)
        response = tokenizer.decode(outputs[0], skip_special_tokens=True)
        return super().parse_json(response)