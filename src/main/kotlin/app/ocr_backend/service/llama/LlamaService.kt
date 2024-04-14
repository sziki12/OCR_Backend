package app.ocr_backend.service.llama

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
class LlamaService {

    private val execLlama = PathHandler.getLlamaStartDir().pathString+"${File.separator}LlamaRunnable.py"
    private val llamaOutputDir = PathHandler.getLlamaOutputDir().pathString
    fun extractItems(imageName: String, items: List<String>): String {
        //Remove Accents from Input, because Llama handles Accented chars the wrong way
        println(execLlama)
        var itemsToProcess = ""
        for(item in items)
        {
            itemsToProcess += StringUtils.stripAccents(item)+"\n"
        }
        val inputName = imageName.replace(".jpg", ".txt")
        val outFile = File(llamaOutputDir + "${File.separator}$inputName")

        println("Before Start")
        val process = llamaProcessBuilder(itemsToProcess, outFile).start()
        process.waitFor()
        println("Finished")
        return extractJson(outFile)
    }

    fun extractJson(outFile: File):String
    {
        val outputScanner = Scanner(outFile, StandardCharsets.UTF_8)
        println("READING")
        var out = ""
        var indentLevel = 0
        var found = false
        var toAdd = false
        while(outputScanner.hasNextLine()&&!found)
        {
            val next = outputScanner.nextLine()
            var line = "$next \n"
            println("$indentLevel TEST: $line")

            if(next.contains("{"))
            {
                if(indentLevel<=0)
                {
                    val beginIndex = next.indexOf('{')
                    line = line.substring(beginIndex)
                }
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
        println(out)
        println("READ")
        outputScanner.close()
        return out
    }

    private fun llamaProcessBuilder(receiptText: String, outFile: File):ProcessBuilder
    {
        val processBuilder =  ProcessBuilder("python",execLlama,"--receiptText",receiptText,"--pathToModel",PathHandler.getModelDir().pathString)
        processBuilder.redirectOutput(outFile)
        processBuilder.redirectErrorStream(true)
        return processBuilder
    }
}