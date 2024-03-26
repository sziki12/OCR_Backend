package app.ocr_backend.service

import app.ocr_backend.utils.PathHandler
import org.apache.commons.lang3.StringUtils
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

    private val llamaPrompt = "Please extract from the given receipt the items name as string, cost as Number and quantity as Number in a JSON format:"

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
        val process = ocrProcessBuilder(imageName).start()
        val outScanner = Scanner(process.inputStream, StandardCharsets.UTF_8)
        var out = ""
        while(outScanner.hasNextLine())
            out += outScanner.nextLine()+"\n"
        outScanner.close()

        return out
    }

    fun extractItems(imageName:String,items:String): String {
        //Remove Accents from Input, because Llama handles Accented chars the wrong way
        val itemsToProcess = StringUtils.stripAccents(items)
        val inputName = imageName.replace(".jpg",".txt")
        val inputFile = File(llamaInputDir+"${File.separator}$inputName")
        val outFile = File(llamaOutputDir+"${File.separator}$inputName")

        val pw = PrintWriter(
            OutputStreamWriter(
                inputFile.outputStream(), StandardCharsets.UTF_8), true)
        pw.write(llamaPrompt+"\n")
        pw.write(itemsToProcess+"\n")
        pw.write("\\")
        pw.close()

        val process = llamaProcessBuilder(inputFile,outFile).start()
        Thread.sleep(60000)

        val outputScanner = Scanner(outFile, StandardCharsets.UTF_8)
        println("READING")
        var out = ""
        var indentLevel = 0
        var found = false
        var toAdd = false
        while(outputScanner.hasNextLine()&&!found)
        {
            val next = outputScanner.nextLine()
            val line = "$next \n"

            if(next.contains("{"))
            {
                toAdd=true
                indentLevel++
            }

            if(next.contains("}"))
            {
                toAdd=true
                indentLevel--
                if(indentLevel<=0)
                    found=true
            }

            if(indentLevel>0)
            {
                toAdd=true
            }

            if(toAdd)
            {
                out += line
            }
        }
        println("READ")

        outputScanner.close()
        process.destroy()
        return out
    }


    //!!!!!!!!!!!!!!!!!!!!!!!!!!        TEST        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    fun test(): String {
        var counter = 1
        var outFile = File(llamaOutputDir+"${File.separator}szamla0$counter.txt")
        while(outFile.exists())
        {
            counter++
            outFile = File(llamaOutputDir+"${File.separator}szamla0$counter.txt")
        }
        val inFile = File(llamaInputDir+"${File.separator}szamla0110.txt")

        val process = llamaProcessBuilder(inFile,outFile).start()
        Thread.sleep(60000)
        val outputScanner = Scanner(outFile, StandardCharsets.UTF_8)

        var out = ""
        var indentLevel = 0
        var found = false
        var toAdd = false
        while(outputScanner.hasNextLine()&&!found)
        {
            val next = outputScanner.nextLine()
            val line = "$next \n"
            println("TEST: "+line)
            if(next.contains("{"))
            {
                toAdd=true
                indentLevel++
            }

            if(next.contains("}"))
            {
                toAdd=true
                indentLevel--
                if(indentLevel<=0)
                    found=true
            }

            if(indentLevel>0)
            {
                toAdd=true
            }

            if(toAdd)
            {
                out += line
                println("OUT: "+line)
            }
        }
        outputScanner.close()
        process.destroy()
        return out
    }
    //!!!!!!!!!!!!!!!!!!!!!!!!!!        TEST        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    private fun llamaProcessBuilder(inFile:File,outFile:File):ProcessBuilder
    {
        val processBuilder =  ProcessBuilder(execLlama)
        processBuilder.directory(PathHandler.getLlamaRunnableDir().toFile())
        processBuilder.redirectInput(inFile)
        processBuilder.redirectOutput(outFile)
        processBuilder.redirectErrorStream(true)
        return processBuilder
    }

    private fun ocrProcessBuilder(imageName:String):ProcessBuilder
    {
        val processBuilder = ProcessBuilder("python",execPythonPath,"--image",imageName,"--path",imagePath)
        processBuilder.redirectErrorStream(true)
        processBuilder.environment()["PYTHONIOENCODING"] = "utf-8"
        return processBuilder
    }
}