package app.ocr_backend.service.ocr

import app.ocr_backend.dto.OcrResponse
import app.ocr_backend.model.OcrEntity
import app.ocr_backend.utils.PathHandler
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.io.path.pathString


@Service
class OcrService {

    private val execPythonPath = PathHandler.getOcrStartDir().pathString+"${File.separator}OcrRunnable.py"
    private val imagePath = PathHandler.getImageDir().pathString
    private var mainSeparator = OcrEntity.mainSeparator
    private var itemSeparator = OcrEntity.itemSeparator
    fun processImage(imageName: String, newReceiptId: Long): OcrResponse {
        val process = ocrProcessBuilder(imageName).start()
        val outScanner = Scanner(process.inputStream, StandardCharsets.UTF_8)
        var out = ""
        while (outScanner.hasNextLine())
            out += outScanner.nextLine() + "\n"
        outScanner.close()
        val output = out.split(mainSeparator)
        println("output: $out")

        var date = LocalDate.now()
        extractDate(output[4])?.let {
            println("DATE: $it")
            date=LocalDate.ofInstant(it.toInstant(ZoneOffset.UTC),ZoneOffset.UTC)
        }

        return OcrResponse(
            plainText = output[1].split("\n"),
            filteredReceipt = output[2].split("\n"),
            extractedItems = output[3].split(itemSeparator),
            date = date,
            newReceiptId
        )
    }

    private fun extractDate(inDate:String):LocalDateTime?
    {
        if(inDate=="None")
            return null
        try {
            var date = inDate.replace(Regex("[.,-]"),"")
            date = date.replace(Regex("[ ]+")," ")
            date = date.replace("\n","")
            val parts = date.split(" ")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            if(date.contains(":"))
            {
                val other = parts[3].split(":")
                val hour = other[0].toInt()
                val min = other[1].toInt()
                return LocalDateTime.of(year, month, day,hour,min)
            }
            return LocalDateTime.of(year, month, day,0,0)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
            return null
        }
    }

    private fun ocrProcessBuilder(imageName:String):ProcessBuilder
    {
        val processBuilder = ProcessBuilder("python",execPythonPath,
            "--image",imageName,
            "--path",imagePath,
            "--separator",mainSeparator,
            "--itemseparator",itemSeparator)
        processBuilder.redirectErrorStream(true)
        processBuilder.environment()["PYTHONIOENCODING"] = "utf-8"
        return processBuilder
    }
}