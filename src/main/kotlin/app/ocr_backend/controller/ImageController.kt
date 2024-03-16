package app.ocr_backend.controller

import app.ocr_backend.dto.OcrResponse
import app.ocr_backend.model.Receipt
import app.ocr_backend.service.ReceiptService
import app.ocr_backend.utils.PathHandler
import com.google.gson.Gson
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import kotlin.io.path.pathString

@RestController
@RequestMapping("/api/receipt")
@CrossOrigin
class ImageController(private val service: ReceiptService) {
    private final val modelController = ModelController()
    private val gson = Gson()

    private final val separator = "======"
    private final val itemSeparator = "------"
    init {
        modelController.setSeparators(separator,itemSeparator)
    }

    @PostMapping("/image")
    fun uploadImage(@RequestParam("file") image: MultipartFile): ResponseEntity<String> {
        val altName = "file.jpg"
        val file = File(PathHandler.getImageDir().pathString + File.separator + (image.originalFilename?:altName))
        image.transferTo(file)

        val output = modelController.processImage(image.originalFilename?:altName).split(separator)

        val newReceiptId = service.saveReceipt(Receipt()).id
        val ocrOutput = OcrResponse(
            plainText = output[1].split("\n"),
            filteredReceipt = output[1].split("\n"),
            extractedItems = output[2].split(itemSeparator),
            newReceiptId
        )

        val json: String = gson.toJson(ocrOutput)
        return ResponseEntity.ok().body(json)
    }
}