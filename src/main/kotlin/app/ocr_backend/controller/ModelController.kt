package app.ocr_backend.controller

import app.ocr_backend.utils.PathHandler
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.io.path.pathString


class ModelController {
    private val execPath = PathHandler.getPythonDir().pathString+"${File.separator}Runnable.py"
    private val imagePath = PathHandler.getImageDir().pathString
    fun processImage(params:String):String
    {
        val processBuilder = ProcessBuilder("python",execPath,"--image",params,"--path",imagePath)
        processBuilder.redirectErrorStream(true)
        processBuilder.environment()["PYTHONIOENCODING"] = "utf-8"

        val process = processBuilder.start()
        val outScanner = Scanner(process.inputStream, StandardCharsets.UTF_8)
        var out = ""
        while(outScanner.hasNextLine())
            out += outScanner.nextLine()+"\n"

        println(out)
        outScanner.close()
        return out
    }
}