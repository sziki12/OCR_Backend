from http.server import BaseHTTPRequestHandler, HTTPServer
import time
import json
from types import SimpleNamespace
from Ocr.Tesseract.TesseractOcrProcessor import TesseractOcrProcessor
from Ocr.Paddle.PaddleOcrProcessor import PaddleOcrProcessor

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
            self.args["openai_api_key"] = parsed_body.openai_api_key
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

        print(self.args)    
    def do_POST(self):
        self.init_params()
        
        if self.path == "/ocr":
            receiptText = ""
            ocr_type = self.args["ocr_type"]

            if ocr_type == "tesseract":
                ocr = TesseractOcrProcessor(self.args)
                receiptText = ocr.read_receipt_with_tesseract() 
                
            elif ocr_type == "paddle":	
                ocr = PaddleOcrProcessor(self.args)
                receiptText = ocr.readReceiptWithPaddle()

            self.send_response(200)
            self.send_header("Content-type", "text/json")
            self.end_headers() 
            self.wfile.write(bytes(receiptText, 'utf-8'))

if __name__ == "__main__":        
    webServer = HTTPServer((hostName, serverPort), PythonWebServer)
    print("Server started http://%s:%s" % (hostName, serverPort))

    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()
    print("Server stopped.")   