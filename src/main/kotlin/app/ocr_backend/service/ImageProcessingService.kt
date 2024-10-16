package app.ocr_backend.service

import app.ocr_backend.dto.LlamaItemList
import app.ocr_backend.dto.OcrResponse
import app.ocr_backend.model.OcrEntity
import app.ocr_backend.model.Receipt
import app.ocr_backend.service.llama.LlamaService
import app.ocr_backend.service.ocr.OcrService
import app.ocr_backend.utils.PathHandler
import com.google.gson.Gson
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import kotlin.io.path.pathString


@Service
class ImageProcessingService(
    val ocrService: OcrService,
    val llamaService: LlamaService,
    val service: DBService
) {
    private val maxRetryCount:Int = 10
    private val gson = Gson()
    fun processImage(image: MultipartFile): String {
        val optReceipt = service.saveReceipt(Receipt().also { it.isPending = true })
        return if(optReceipt.isPresent)
        {
            val newReceipt = optReceipt.get()
            val fileName = service.generateImageName(newReceipt, image)
            val file = File(PathHandler.getImageDir().pathString + File.separator + fileName)

            image.transferTo(file)

            val ocrOutput = ocrService.processImage(fileName, newReceipt.id)

            service.saveOcrEntity(newReceipt.id,OcrEntity.fromOcrResponse(ocrOutput))
            service.saveImage(newReceipt.id, fileName)

            parseResponse(fileName,newReceipt, ocrOutput)

            service.updateReceipt(newReceipt.also {
                it.isPending = false
            })
            gson.toJson(ocrOutput)
        }
        else
        {
            ""
        }
    }

    private fun parseResponse(fileName:String, newReceipt:Receipt,ocrOutput:OcrResponse,numberOfRun:Int = 1)
    {
        val itemsJson = llamaService.extractItems(fileName, ocrOutput.extractedItems)
        try
        {
            val llamaItemList = gson.fromJson(itemsJson, LlamaItemList::class.java)
            println(llamaItemList)
            for (item in llamaItemList.toItemList())
            {
                service.saveItem(newReceipt.id, item)
            }
        } catch (e: Exception)
        {
            System.err.println("Failed to read JSON from Llama for image: $fileName")
            if(numberOfRun < maxRetryCount)
            {
                parseResponse(fileName, newReceipt, ocrOutput, numberOfRun + 1)
            }
            //Failed to read JSON
        }
    }
}