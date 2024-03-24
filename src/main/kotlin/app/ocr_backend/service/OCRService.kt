package app.ocr_backend.service

import app.ocr_backend.utils.PathHandler
import org.springframework.stereotype.Service
import java.io.File
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.io.path.pathString


@Service
class OCRService {
    private val execPythonPath = PathHandler.getPythonDir().pathString+"${File.separator}Runnable.py"
    private val execLlama = PathHandler.getLlamaStartDir().pathString+"${File.separator}llama_runnable.bat"
    private val llamaInputDir = PathHandler.getLlamaInputDir().pathString
    private val llamaOutputDir = PathHandler.getLlamaOutputDir().pathString
    private val imagePath = PathHandler.getImageDir().pathString
    private var mainSeparator = "======"
    private var itemSeparator = "------"

    private val llamaPrompt = "Please extract from the given hungarian receipt the items name, price and quantity in a JSON format:"

    fun setSeparators(mainSeparator:String,itemSeparator:String)
    {
        this.mainSeparator=mainSeparator
        this.itemSeparator=itemSeparator
    }
    fun processImage(imageName:String):String
    {
        val processBuilder = ProcessBuilder("python",execPythonPath,"--image",imageName,"--path",imagePath)
        processBuilder.redirectErrorStream(true)
        processBuilder.environment()["PYTHONIOENCODING"] = "utf-8"
        val process = processBuilder.start()
        val outScanner = Scanner(process.inputStream, StandardCharsets.UTF_8)
        var out = ""
        while(outScanner.hasNextLine())
            out += outScanner.nextLine()+"\n"
        outScanner.close()

        return out
    }

    fun extractItems(imageName:String,items:String)
    {
        val inputName = imageName.replace(".jpg",".txt")
        val input = File(llamaInputDir+"${File.separator}$inputName")

        val pw = PrintWriter(input)
        pw.write(llamaPrompt+"\n")
        pw.write(items+"\n")
        pw.write("\\")
        pw.close()

        val processBuilder =  ProcessBuilder(execLlama,llamaInputDir,llamaOutputDir,inputName)
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()
        Thread.sleep(5000)
        process.destroy()
        val outScanner = Scanner(process.inputStream, StandardCharsets.UTF_8)
        var out = ""
        while(outScanner.hasNextLine())
            out += outScanner.nextLine()+"\n"
        outScanner.close()

        println(out)
    }
}