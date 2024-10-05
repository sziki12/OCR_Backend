from transformers import AutoModelForSeq2SeqLM, AutoTokenizer,T5ForConditionalGeneration,TFMT5ForConditionalGeneration
from Llm.LlmBase import LlmBase

class T5ReceiptProcessor(LlmBase):

    def process(self,receipt_text):
        path_to_model = "D:\\huggingface\\hub\\models--t5-3b\\snapshots\\bed96aab9ee46012a5046386105ee5fd0ac572f0"
        
        tokenizer = AutoTokenizer.from_pretrained("t5-3b")#"t5-3b"
        model = T5ForConditionalGeneration.from_pretrained(path_to_model)
        input_ids = tokenizer(str(super().get_process_prompt(receipt_text)), return_tensors="pt").input_ids 
        outputs = model.generate(input_ids)
        return super().parse_json(tokenizer.decode(outputs[0], skip_special_tokens=True))