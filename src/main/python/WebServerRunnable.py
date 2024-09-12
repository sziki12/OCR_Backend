from http.server import BaseHTTPRequestHandler, HTTPServer
import time
import json
from types import SimpleNamespace
from Ocr.TesseractOcrProcessor import TesseractOcrProcessor
from Ocr.PaddleOcrProcessor import PaddleOcrProcessor

hostName = "localhost"
serverPort = 9090

class PythonWebServer(BaseHTTPRequestHandler):
    def do_POST(self):

        args = {}
        content_len = int(self.headers.get('Content-Length'))
        request_body = self.rfile.read(content_len).decode('utf-8')
        parsed_body = json.loads(request_body, object_hook=lambda d: SimpleNamespace(**d))
        print(parsed_body)
        args["image"] =  parsed_body.image
        args["path"] = parsed_body.path
        args["ocr_type"] = parsed_body.ocr_type
        args["openai_api_key"] = parsed_body.openai_api_key
        #args["separator"] = parsed_body.separator
        #args["itemseparator"] = parsed_body.itemseparator
        #args["debug"] = parsed_body.debug
        """ form = cgi.FieldStorage(
            fp=self.rfile,
            headers=self.headers,
            environ={'REQUEST_METHOD': 'POST',
                     'CONTENT_TYPE': self.headers['Content-Type'],
                     }
        )
        args["image"] =  form.getvalue("image")
        args["path"] = form.getvalue("path")
        args["ocr_type"] = form.getvalue("ocr_type")
        args["separator"] = form.getvalue("separator")
        args["itemseparator"] = form.getvalue("itemseparator")
        args["debug"] = form.getvalue("debug") """

        if self.path == "/ocr":
            receiptText = ""
            ocr_type = args["ocr_type"]
            if ocr_type == "tesseract":
                ocr = TesseractOcrProcessor(args)
                receiptText = ocr.readReceiptWithTesseract() 
                
            elif ocr_type == "paddle":	
                ocr = PaddleOcrProcessor(args)
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