package app.ocr_backend.controller

import app.ocr_backend.utils.PathHandler
import java.io.File
import java.util.*
import kotlin.io.path.pathString


class ModelController {
    private val execPath = PathHandler.getPythonDir().pathString+"${File.separator}Runnable.py"
    private val imagePath = PathHandler.getImageDir().pathString
    //TODO Unreadable Characters maybe wrong charset?
    fun processImage(params:String):String
    {
        val processBuilder = ProcessBuilder("python",execPath,"--image",params,"--path",imagePath)
        processBuilder.redirectErrorStream(true)

        val process = processBuilder.start()
        val outScanner = Scanner(process.inputStream)
        var out = ""
        while(outScanner.hasNextLine())
            out += outScanner.nextLine()+"\n"
        return out
    }
}