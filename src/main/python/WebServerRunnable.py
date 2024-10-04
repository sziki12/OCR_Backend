from http.server import BaseHTTPRequestHandler, HTTPServer
import json
from types import SimpleNamespace
from Ocr.Tesseract.TesseractOcrProcessor import TesseractOcrProcessor
from Ocr.Paddle.PaddleOcrProcessor import PaddleOcrProcessor
from Llm.MistralReceiptProcessor import MistralReceiptProcessor
from Llm.ChatGptReceiptProcessor import ChatGptReceiptProcessor
from Llm.GeminiReceiptProcessor import GeminiReceiptProcessor
from Llm.OcrResponse import OcrResposne
from Llm.LlamaReceiptProcessor import LlamaReceiptProcessor

hostName = "localhost"
serverPort = 9090

class PythonWebServer(BaseHTTPRequestHandler):

    def do_POST(self):
        self.init_params()

        if self.path == "/ocr":
            self.ocr_request()

        if self.path == "/categorise":
            self.categorise_request() 

    def ocr_request(self):
        #Process Image with Ocr model
        ocr_response = self.process_ocr_request()
        
        #Process extracted text with Llm
        if(ocr_response.response_json == None):
            ocr_response = self.process_data_extraction()
            #Construct response
            response_json = json.dumps({
                "processed_receipt": json.loads(ocr_response.processed_text),
                "receipt_text": ocr_response.receipt_text
            })        

        self.send_response(200)
        self.send_header("Content-type", "text/json")
        self.end_headers() 
        self.wfile.write(bytes(response_json, 'utf-8'))

    def categorise_request(self):
        response_json = self.process_categorise_request()

        self.send_response(200)
        self.send_header("Content-type", "text/json")
        self.end_headers() 
        self.wfile.write(bytes(response_json, 'utf-8'))            

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
            self.args["ocr_type"] = parsed_body.ocr_type
        except:
            pass
        try:
            self.args["orientation"] = parsed_body.orientation
        except:
            self.args["orientation"] = "portrait"
        try:
            self.args["parse_model"] = parsed_body.parse_model
        except:
            pass
        try:
            self.args["api_key"] = parsed_body.api_key
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
            self.args["categorise_model"] = parsed_body.categorise_model
        except:
            pass
        """try:
            self.args["separator"] = parsed_body.separator
        except:
            self.args["separator"] = "///"
        try:
            self.args["itemseparator"] = parsed_body.itemseparator
        except:
            self.args["itemseparator"] = "---"
            """
        try:
            self.args["debug"] = int(parsed_body.debug)
        except:
            self.args["debug"] = 0

        print("ARGS:\n"+str(self.args)+"\n")  

    

    def process_ocr_request(self):
        api_key = self.args["api_key"]
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
                processor = MistralReceiptProcessor(api_key)
                response_json = processor.process_from_image(image_path)
            case "gemini":
                processor = GeminiReceiptProcessor(api_key)
                response_json = processor.process_from_image(image_path)   

        return OcrResposne(receipt_text, None, response_json)  

    def process_data_extraction(self, ocr_response):
        parse_model = self.args["parse_model"]
        api_key = self.args["api_key"]
        if("gpt" in parse_model):
                processor = ChatGptReceiptProcessor(api_key,parse_model)
        elif("llama" in parse_model):
            processor = LlamaReceiptProcessor(parse_model)
        else:
            match parse_model:
                case "mistral":
                    processor = MistralReceiptProcessor(api_key)
                case "gemini":
                    processor = GeminiReceiptProcessor(api_key)
                case "gorilla":
                    pass
        processed_text = processor.process(ocr_response.receipt_text)
        print("#####PROCESSED#########")
        print(processed_text)
        ocr_response.processed_text = processed_text
        return ocr_response

    def process_categorise_request(self):
        api_key = self.args["api_key"]
        items = self.args["items"]
        categories = self.args["categories"]
        categorise_model = self.args["categorise_model"]
        if("gpt" in categorise_model):
            processor = ChatGptReceiptProcessor(api_key, categorise_model)
        elif("llama" in categorise_model):
            processor = LlamaReceiptProcessor(categorise_model)
        else:
            match categorise_model:
                case "mistral":
                    processor = MistralReceiptProcessor(api_key)
                case "gemini":
                    processor = GeminiReceiptProcessor(api_key)
                case "gorilla":
                    pass
        categorised_items = processor.categorise(items, categories)
        return categorised_items

       

if __name__ == "__main__":        
    webServer = HTTPServer((hostName, serverPort), PythonWebServer)
    print("Server started http://%s:%s" % (hostName, serverPort))

    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()
    print("Server stopped.")   