package app.ocr_backend.service

import app.ocr_backend.utils.PathHandler
import org.springframework.stereotype.Service
import java.io.File
import java.io.OutputStreamWriter
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

    private val llamaPrompt = "Please extract from the given hungarian receipt the items name as string, price as Number and quantity as Number in a JSON format using only UTF-8 characters without accents:"

    init {
        val param = File(PathHandler.getLlamaStartDir().pathString+"${File.separator}param.txt")
        val pw = PrintWriter(param)
        pw.write("InputPath=$llamaInputDir\n")
        pw.write("OutputPath=$llamaOutputDir\n")
        pw.write("FirstResponse=First Response\n")
        pw.close()
    }

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

    fun extractItems(imageName:String,items:String): String {
        val inputName = imageName.replace(".jpg",".txt")
        val inputFile = File(llamaInputDir+"${File.separator}$inputName")

        val pw = PrintWriter(
            OutputStreamWriter(
                inputFile.outputStream(), StandardCharsets.UTF_8), true)
        pw.write(llamaPrompt+"\n")
        pw.write(items+"\n")
        pw.write("\\")
        pw.close()

        val outFile = File(llamaOutputDir+"${File.separator}$inputName")

        val processBuilder =  ProcessBuilder(execLlama)
        processBuilder.directory(PathHandler.getLlamaRunnableDir().toFile())
        processBuilder.redirectInput(inputFile)
        processBuilder.redirectOutput(outFile)
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()
        Thread.sleep(60000)
        outFile.createNewFile()
        val outputScanner = Scanner(process.inputStream, StandardCharsets.UTF_8)
        var out = ""
        var indentLevel = 0
        while(outputScanner.hasNextLine())
        {
            val next = outputScanner.nextLine()
            if(indentLevel>0)
                out += next+"\n"
            else if(next.contains("{"))
            {
                out += next+"\n"
                indentLevel++
            }
            else if(next.contains("}"))
            {
                out += next+"\n"
                indentLevel--
            }

        }
        println(out)

        outputScanner.close()
        process.destroy()
        return out
    }
}