package app.ocr_backend.controller

import java.util.*


class ModelController {
    private val execPath = "/Users/dr.sztanyiistvan/Documents/GitHub/OCR_Backend/src/main/resources/python/Runnable.py"
    private val imagePath = "/Users/dr.sztanyiistvan/Documents/GitHub/OCR_Backend/src/main/resources/python/image"

    fun processImage(params:String):String
    {
        val processBuilder = ProcessBuilder("python3",execPath,"--image",params,"--path",imagePath)
        processBuilder.redirectErrorStream(true)

        val process = processBuilder.start()
        val outScanner = Scanner(process.inputStream)
        var out = ""
        while(outScanner.hasNextLine())
            out += outScanner.nextLine()+"\n"
        return out
    }
}