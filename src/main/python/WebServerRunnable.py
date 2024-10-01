from http.server import BaseHTTPRequestHandler, HTTPServer
import json
from types import SimpleNamespace
from Ocr.Tesseract.TesseractOcrProcessor import TesseractOcrProcessor
from Ocr.Paddle.PaddleOcrProcessor import PaddleOcrProcessor
from Llm.MistralReceiptProcessor import MistralReceiptProcessor
from Llm.ChatGptReceiptProcessor import ChatGptReceiptProcessor
from Llm.GeminiReceiptProcessor import GeminiReceiptProcessor

hostName = "localhost"
serverPort = 9090

class PythonWebServer(BaseHTTPRequestHandler):
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
            self.args["separator"] = parsed_body.separator
        except:
            self.args["separator"] = "///"
        try:
            self.args["itemseparator"] = parsed_body.itemseparator
        except:
            self.args["itemseparator"] = "---"
        try:
            self.args["debug"] = int(parsed_body.debug)
        except:
            self.args["debug"] = 0

        print("ARGS:\n"+str(self.args)+"\n")  

    def do_POST(self):
        self.init_params()
        
        if self.path == "/ocr":
            ocr_type = self.args["ocr_type"]
            parse_model = self.args["parse_model"]
            api_key = self.args["api_key"]
            image_path = self.args["path"]+"/"+self.args["image"]
            response_json = None
            #Process Image with Ocr model
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
            print(response_json)
            
            #Process extracted text with Llm
            if(response_json == None):
                if("gpt" in parse_model):
                    processor = ChatGptReceiptProcessor(api_key,parse_model)
                else:
                    match parse_model:
                        case "mistral":
                            processor = MistralReceiptProcessor(api_key)
                        case "gemini":
                            processor = GeminiReceiptProcessor(api_key)
                        case "llama":    
                            pass
                        case "gorilla":
                            pass
                processed_text = processor.process(receipt_text)
                print("#####PROCESSED#########")
                print(processed_text)
                #Construct response
                response_json = json.dumps({
                    "processed_receipt": json.loads(processed_text),
                    "receipt_text": receipt_text
                })        

            self.send_response(200)
            self.send_header("Content-type", "text/json")
            self.end_headers() 
            self.wfile.write(bytes(response_json, 'utf-8'))

if __name__ == "__main__":        
    webServer = HTTPServer((hostName, serverPort), PythonWebServer)
    print("Server started http://%s:%s" % (hostName, serverPort))

    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()
    print("Server stopped.")   