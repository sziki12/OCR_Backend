package app.ocr_backend.ai.ocr

import app.ocr_backend.ai.ocr.backend_dto.OcrParams
import app.ocr_backend.ai.ocr.backend_dto.ProcessedReceipt
import app.ocr_backend.ai.ocr.backend_dto.ReceiptOcrResponse
import app.ocr_backend.ai.ocr.frontend_dto.OcrResponse
import app.ocr_backend.util.PathHandler
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.io.path.pathString


@Service
class OcrService {

    //private val execPythonPath = PathHandler.getOcrStartDir().pathString+"${File.separator}OcrRunnable.py"
    private val url = "http://localhost:9090/ocr"
    @Value("\${openai_api_key}")
    private lateinit var openaiApiKey:String
    private val imagePath = PathHandler.getImageDir().pathString
    //private var mainSeparator = OcrEntity.mainSeparator
    //private var itemSeparator = OcrEntity.itemSeparator
    private val gson = Gson()
    fun processImage(imageName: String, newReceiptId: Long): OcrResponse {
        val receiptOcrResponse = sendOcrRequest(imageName)

        var date = LocalDate.now()
        receiptOcrResponse.processed_receipt.date_of_purchase?.let { dateOfPurchase ->
            extractDate(dateOfPurchase)?.let { processedDate ->
                println("DATE: $processedDate")
                date = LocalDate.ofInstant(processedDate.toInstant(ZoneOffset.UTC),ZoneOffset.UTC)
            }
        }

        //TODO Fix OcrTextResponse content
        return OcrResponse(
            processedReceipt = receiptOcrResponse.processed_receipt,
            receiptText = receiptOcrResponse.receipt_text,
            date = date.toString(),
            newReceiptId = newReceiptId
        )
    }

    private fun extractDate(inDate:String):LocalDateTime?
    {
        if(inDate=="None")
            return null
        try {
            var date = inDate.replace(Regex("[.,-]")," ")
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

    private fun sendOcrRequest(imageName:String): ReceiptOcrResponse {
        val jsoType = "application/json".toMediaType()

        val client = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build()
        val json = gson.toJson(OcrParams(
            ocr_type = "paddle",
            path = imagePath,
            image = imageName,
            openai_api_key = openaiApiKey
        ))
        val body = json.toRequestBody(jsoType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        //TODO Handle IOExcepton
        val response = client.newCall(request).execute()
        println("PYTHON RESPONSE")
        val responseBody = response.body?.string()
        println(responseBody)
        return gson.fromJson(responseBody, ReceiptOcrResponse::class.java)
    }

    /*private fun ocrProcessBuilder(imageName:String):ProcessBuilder
    {
        val processBuilder = ProcessBuilder("python",execPythonPath,
            "--image",imageName,
            "--path",imagePath,
            "--separator",mainSeparator,
            "--itemseparator",itemSeparator)
        processBuilder.redirectErrorStream(true)
        processBuilder.environment()["PYTHONIOENCODING"] = "utf-8"
        return processBuilder
    }*/
}