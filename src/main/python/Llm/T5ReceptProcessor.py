from transformers import AutoModelForSeq2SeqLM, AutoTokenizer,T5ForConditionalGeneration,TFMT5ForConditionalGeneration
from Llm.LlmBase import LlmBase

class T5ReceiptProcessor(LlmBase):

    def process(self,receipt_text):
        return self.text_request(super().get_process_prompt(receipt_text))
    
    def categorise(self, items, categories):
        return self.text_request(super().get_categorise_prompt(items,categories))
    
    def process_composite(self,separator,composite_text):
        return self.text_request(super().get_process_from_composite_prompt(separator,composite_text))
    
    def text_request(self,prompt):
        path_to_model = "D:\\huggingface\\hub\\models--t5-3b\\snapshots\\bed96aab9ee46012a5046386105ee5fd0ac572f0"
        
        tokenizer = AutoTokenizer.from_pretrained("t5-3b")#"t5-3b"
        model = T5ForConditionalGeneration.from_pretrained(path_to_model)
        input_ids = tokenizer(str(prompt), return_tensors="pt").input_ids 
        outputs = model.generate(input_ids)
        return super().parse_json(tokenizer.decode(outputs[0], skip_special_tokens=True))