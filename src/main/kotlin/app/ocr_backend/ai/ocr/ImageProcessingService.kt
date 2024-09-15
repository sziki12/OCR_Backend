package app.ocr_backend.ai.ocr

import app.ocr_backend.receipt.Receipt
import app.ocr_backend.db_service.DBService
import app.ocr_backend.ai.ocr.ocr_entity.OcrEntity
import app.ocr_backend.ai.ocr.frontend_dto.OcrResponse
import app.ocr_backend.item.Item
import app.ocr_backend.util.PathHandler
import com.google.gson.Gson
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import kotlin.io.path.pathString


@Service
class ImageProcessingService(
    val ocrService: OcrService,
    //val llamaService: LlamaService,
    val service: DBService
) {
    private val maxRetryCount: Int = 10
    private val gson = Gson()
    fun processImage(image: MultipartFile): OcrResponse? {
        val optReceipt = service.saveReceipt(Receipt().also { it.isPending = true })
        return if (optReceipt.isPresent) {
            val newReceipt = optReceipt.get()
            val fileName = service.generateImageName(newReceipt, image)
            val file = File(PathHandler.getImageDir().pathString + File.separator + fileName)

            image.transferTo(file)

            val ocrOutput = ocrService.processImage(fileName, newReceipt.id)

            service.saveOcrEntity(newReceipt.id, OcrEntity.fromOcrResponse(ocrOutput))
            service.saveImage(newReceipt.id, fileName)

            parseResponse(fileName, newReceipt, ocrOutput)

            service.updateReceipt(newReceipt.also {
                it.isPending = false
            })
            ocrOutput
        } else {
            null
        }
    }

    /*
          ocrOutput.extractedOcrResponse?.let { extractedReceipt ->
                val itemsToAdd =  mutableListOf<Item>()
                service.updateReceipt(newReceipt.also {newReceipt->
                    newReceipt.items = itemsToAdd
                    newReceipt.dateOfPurchase = extractedReceipt.date_of_purchase
                })
            }
     */

    private fun parseResponse(fileName: String, newReceipt: Receipt, ocrOutput: OcrResponse) {
        ocrOutput.processedReceipt.let {
            var parsedItemCost = 0
            for (item in it.toItemList()) {
                parsedItemCost += item.totalCost
                service.saveItem(newReceipt.id, item)
            }
            if(parsedItemCost != it.total_cost){
                service.saveItem(newReceipt.id, Item(name = "Parse Correction", quantity = 1, totalCost = it.total_cost - parsedItemCost))
            }
        }
    }
}