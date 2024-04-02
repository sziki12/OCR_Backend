package app.ocr_backend.service.ocr

import app.ocr_backend.dto.OcrResponse
import app.ocr_backend.utils.PathHandler
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.io.path.pathString

@Service
class OcrService {

    private val execPythonPath = PathHandler.getPythonDir().pathString+"${File.separator}Runnable.py"
    private val imagePath = PathHandler.getImageDir().pathString
    private var mainSeparator = "======"
    private var itemSeparator = "------"
    fun processImage(imageName: String, newReceiptId: Long): OcrResponse {
        val process = ocrProcessBuilder(imageName).start()
        val outScanner = Scanner(process.inputStream, StandardCharsets.UTF_8)
        var out = ""
        while (outScanner.hasNextLine())
            out += outScanner.nextLine() + "\n"
        outScanner.close()
        val output = out.split(mainSeparator)

        return OcrResponse(
            plainText = output[1].split("\n"),
            filteredReceipt = output[2].split("\n"),
            extractedItems = output[3].split(itemSeparator),
            newReceiptId
        )
    }

    fun setSeparators(mainSeparator:String,itemSeparator:String)
    {
        this.mainSeparator=mainSeparator
        this.itemSeparator=itemSeparator
    }

    private fun ocrProcessBuilder(imageName:String):ProcessBuilder
    {
        val processBuilder = ProcessBuilder("python",execPythonPath,"--image",imageName,"--path",imagePath)
        processBuilder.redirectErrorStream(true)
        processBuilder.environment()["PYTHONIOENCODING"] = "utf-8"
        return processBuilder
    }
}