package app.ocr_backend.ai.ocr

import app.ocr_backend.ai.ocr.backend_dto.OcrParams
import app.ocr_backend.ai.ocr.backend_dto.ReceiptOcrResponse
import app.ocr_backend.ai.ocr.frontend_dto.OcrResponse
import app.ocr_backend.util.PathHandler
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.io.path.pathString


@Service
class OcrService {

    //private val execPythonPath = PathHandler.getOcrStartDir().pathString+"${File.separator}OcrRunnable.py"
    private val url = "http://localhost:9090/ocr/process"

    private val imagePath = PathHandler.getImageDir().pathString

    //private var mainSeparator = OcrEntity.mainSeparator
    //private var itemSeparator = OcrEntity.itemSeparator
    private val gson = Gson()
    fun processImage(imageName: String, ocrParams: OcrParams, newReceiptId: Long): OcrResponse {
        val receiptOcrResponse = sendOcrRequest(imageName, ocrParams)

        return OcrResponse(
            processedReceipt = receiptOcrResponse.processed_receipt,
            receiptText = receiptOcrResponse.receipt_text,
            date = receiptOcrResponse.processed_receipt.date_of_purchase ?: LocalDate.now().toString(),
            newReceiptId = newReceiptId
        )
    }

    fun extractDate(inDate: String): LocalDateTime? {
        if (inDate == "None")
            return null
        try {
            var date = inDate.replace(Regex("[.,-]"), " ")
            date = date.replace(Regex("[ ]+"), " ")
            date = date.replace("\n", "")
            val parts = date.split(" ")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            if (date.contains(":")) {
                val other = parts[3].split(":")
                val hour = other[0].toInt()
                val min = other[1].toInt()
                return LocalDateTime.of(year, month, day, hour, min)
            }
            return LocalDateTime.of(year, month, day, 0, 0)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun sendOcrRequest(imageName: String, ocrParams: OcrParams): ReceiptOcrResponse {
        val jsoType = "application/json".toMediaType()

        val client = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build()
        val json = gson.toJson(
            OcrParams(
                orientation = ocrParams.orientation,
                ocr_type = ocrParams.ocr_type,
                parse_model = ocrParams.parse_model,
                path = imagePath,
                image = imageName,
            )
        )
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