package app.ocr_backend.ai.ocr

import app.ocr_backend.ai.ocr.dto.OcrParams
import app.ocr_backend.ai.ocr.ocr_entity.OcrEntity
import app.ocr_backend.ai.ocr.response.ExtractedOcrResponse
import app.ocr_backend.ai.ocr.response.OcrResponse
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
    private var mainSeparator = OcrEntity.mainSeparator
    private var itemSeparator = OcrEntity.itemSeparator
    private val gson = Gson()
    fun processImage(imageName: String, newReceiptId: Long): OcrResponse {
        val extractedResponse = sendOcrRequest(imageName)

        var date = LocalDate.now()
        extractedResponse?.date_of_purchase?.let { dateOfPurchase ->
            extractDate(dateOfPurchase)?.let { processedDate ->
                println("DATE: $processedDate")
                date = LocalDate.ofInstant(processedDate.toInstant(ZoneOffset.UTC),ZoneOffset.UTC)
            }
        }

        //TODO Fix OcrTextResponse content
        return OcrResponse(
            extractedOcrResponse = extractedResponse,
            plainText = listOf("plainText"),//output[1].split("\n"),
            filteredReceipt = listOf("filteredReceipt"),//output[2].split("\n"),
            extractedItems = listOf("extractedItems"),//output[3].split(itemSeparator),
            date = date.toString(),
            newReceiptId
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

    private fun sendOcrRequest(imageName:String): ExtractedOcrResponse? {
        val jsoType = "application/json".toMediaType();

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
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
        val response_body = response.body?.string()
        println(response_body)
        return gson.fromJson(response_body, ExtractedOcrResponse::class.java)
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