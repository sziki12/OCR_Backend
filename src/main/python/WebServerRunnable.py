
from torch import Tensor
from dotenv import load_dotenv
import os
from http.server import BaseHTTPRequestHandler, HTTPServer
import json
from types import SimpleNamespace

from Ocr.TesseractOcrProcessor import TesseractOcrProcessor
from Ocr.PaddleOcrProcessor import PaddleOcrProcessor
from Llm.MistralReceiptProcessor import MistralReceiptProcessor
from Llm.ChatGptReceiptProcessor import ChatGptReceiptProcessor
from Llm.GeminiReceiptProcessor import GeminiReceiptProcessor
from Llm.OcrResponse import OcrResposne
from Llm.LlamaReceiptProcessor import LlamaReceiptProcessor
from Llm.LlavaReceiptProcessor import LlavaReceiptProcessor
from Llm.T5ReceptProcessor import T5ReceiptProcessor
from Llm.ClaudeReceiptProcessor import ClaudeReceiptProcessor

hostName = "localhost"
serverPort = 9090

class PythonWebServer(BaseHTTPRequestHandler):

    def do_POST(self):
        self.init_params()

        if self.path == "/ocr":
            self.ocr_request()

        if self.path == "/ocr/process":
            self.ocr_and_process_request()

        if self.path == "/process":
            self.process_request()    

        if self.path == "/categorise":
            self.categorise_request() 

    """
    
    """
    def ocr_request(self):
        #Process Image with Ocr model
        ocr_response = self.execute_ocr_request()
        response_json = json.dumps({
            "receipt_text": cr_response.receipt_text
        })  
        self.send_response_json(200,response_json)  

    """
    
    """
    def process_request(self):
        receipt_text = self.args["receipt_text"]
        ocr_response = OcrResposne(receipt_text, None, None)
        ocr_response = self.execute_data_extraction(ocr_response)
        #Construct response
        response_json = json.dumps({
            "processed_receipt": json.loads(ocr_response.processed_text),
            "receipt_text": ocr_response.receipt_text
        })
        self.send_response_json(200,response_json)  

    """
    
    """
    def ocr_and_process_request(self):
        #Process Image with Ocr model
        ocr_response = self.execute_ocr_request()
        
        #Process extracted text with Llm
        if(ocr_response.response_json == None):
            ocr_response = self.execute_data_extraction(ocr_response)
            #Construct response
            response_json = json.dumps({
                "processed_receipt": json.loads(ocr_response.processed_text),
                "receipt_text": ocr_response.receipt_text
            })  
        else:
            response_json = ocr_response.response_json 

        self.send_response_json(200,response_json)     

    """
    
    """
    def categorise_request(self):
        response_json = self.execute_categorise_request()
        self.send_response_json(200,response_json)           

    """
    
    """
    def init_params(self):
        self.args = {}
        content_len = int(self.headers.get('Content-Length'))
        request_body = self.rfile.read(content_len).decode('utf-8')
        parsed_body = json.loads(request_body, object_hook=lambda d: SimpleNamespace(**d))
        try:
            self.args["image"] =  parsed_body.image
        except:
            pass
        try:    
            self.args["path"] = parsed_body.path
        except:
            pass    
        try:
            self.args["ocr_type"] = parsed_body.ocr_type.lower()
        except:
            pass
        try:
            self.args["orientation"] = parsed_body.orientation.lower()
        except:
            self.args["orientation"] = "portrait"
        try:
            self.args["parse_model"] = parsed_body.parse_model.lower()
        except:
            pass
        try:
            self.args["items"] = parsed_body.items
        except:
            pass
        try:
            self.args["categories"] = parsed_body.categories
        except:
            pass
        try:
            self.args["categorise_model"] = parsed_body.categorise_model.lower()
        except:
            pass
        try:
            self.args["receipt_text"] = parsed_body.receipt_text
        except:
            pass
        try:
            self.args["debug"] = int(parsed_body.debug)
        except:
            self.args["debug"] = 0

        print("ARGS:\n"+str(self.args)+"\n")  

    
    """
    
    """
    def execute_ocr_request(self):
        self.check_params(["path","image","ocr_type"])

        image_path = self.args["path"]+"/"+self.args["image"]
        ocr_type = self.args["ocr_type"]
        receipt_text = None
        response_json = None

        match ocr_type:
            case "tesseract":
                ocr = TesseractOcrProcessor(self.args)
                receipt_text = ocr.read_receipt_with_tesseract() 
            case "paddle":	    
                ocr = PaddleOcrProcessor(self.args)
                receipt_text = ocr.read_receipt_with_paddle()
            case "mistral":
                processor = MistralReceiptProcessor()
                response_json = processor.process_from_image(image_path)
            case "gemini":
                processor = GeminiReceiptProcessor()
                response_json = processor.process_from_image(image_path)
            case "llava":
                processor = LlavaReceiptProcessor()
                response_json = processor.process_from_image(image_path)
            case _:
                    self.send_response_json(401,json.dumps({
                        "ocr_type":"Doesn't match any available model"
                    }))              

        return OcrResposne(receipt_text, None, response_json)  


    """

    """
    def execute_data_extraction(self, ocr_response):
        self.check_params(["parse_model"])

        parse_model = self.args["parse_model"]
        if("gpt" in parse_model):
            processor = ChatGptReceiptProcessor(parse_model)
        elif("llama" in parse_model):
            processor = LlamaReceiptProcessor(parse_model)
        else:
            match parse_model:
                case "mistral":
                    processor = MistralReceiptProcessor()
                case "gemini":
                    processor = GeminiReceiptProcessor()
                case "llava":
                    processor = LlavaReceiptProcessor()    
                case "gorilla":
                    pass
                case "t5":
                    processor = T5ReceiptProcessor()
                case "claude":
                    processor = ClaudeReceiptProcessor()
                case _:
                    self.send_response_json(401,json.dumps({
                        "parse_model":"Doesn't match any available model"
                    })) 
                    return   
        processed_text = processor.process(ocr_response.receipt_text)
        ocr_response.processed_text = processed_text
        return ocr_response

    """
    
    """
    def execute_categorise_request(self):
        self.check_params(["items","categories","categorise_model"])

        items = self.args["items"]
        categories = self.args["categories"]
        categorise_model = self.args["categorise_model"]

        if("gpt" in categorise_model):
            processor = ChatGptReceiptProcessor(categorise_model)
        elif("llama" in categorise_model):
            processor = LlamaReceiptProcessor(categorise_model)
        else:
            match categorise_model:
                case "mistral":
                    processor = MistralReceiptProcessor()
                case "gemini":
                    processor = GeminiReceiptProcessor()
                case "llava":
                    processor = LlavaReceiptProcessor()     
                case "gorilla":
                    pass
                case _:
                    self.send_response_json(401,json.dumps({
                        "categorise_model":"Doesn't match any available model"
                    }))
                    return
        categorised_items = processor.categorise(items, categories)
        return categorised_items
    

    def send_response_json(self,code,response_json):
        self.send_response(code)
        self.send_header("Content-type", "text/json")
        self.end_headers() 
        self.wfile.write(bytes(response_json, 'utf-8'))

    def check_params(self, params):
        for param in params:
            if(self.args[param] == None):
                self.send_response_json(401,json.dumps({
            param : "Not provided"
            }) )
            return    

       

if __name__ == "__main__":      

    ### Load the .env file
    load_dotenv()  
    webServer = HTTPServer((hostName, serverPort), PythonWebServer)
    print("Server started http://%s:%s" % (hostName, serverPort))
    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()
    print("Server stopped.")