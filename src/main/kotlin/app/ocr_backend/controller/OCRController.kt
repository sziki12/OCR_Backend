package app.ocr_backend.controller

import app.ocr_backend.dto.OcrResponse
import app.ocr_backend.model.Receipt
import app.ocr_backend.service.DBService
import app.ocr_backend.service.OCRService
import app.ocr_backend.utils.PathHandler
import com.google.gson.Gson
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import kotlin.io.path.pathString

@RestController
@RequestMapping("/api")
@CrossOrigin
class OCRController(private val service: DBService) {
    private final val ocrService = OCRService()
    private val gson = Gson()

    private final val separator = "======"
    private final val itemSeparator = "------"
    init {
        //modelController.setSeparators(separator,itemSeparator)
    }

    @PostMapping("/image")
    fun uploadImage(@RequestParam("file") image: MultipartFile): ResponseEntity<String> {

        val newReceipt = service.saveReceipt(Receipt())
        val fileName = service.generateImageName(newReceipt,image)
        val file = File(PathHandler.getImageDir().pathString + File.separator + fileName)

        image.transferTo(file)

        val output = ocrService.processImage(fileName).split(separator)

        service.saveImage(newReceipt.id,fileName)

        val ocrOutput = OcrResponse(
            plainText = output[1].split("\n"),
            filteredReceipt = output[2].split("\n"),
            extractedItems = output[3].split(itemSeparator),
            newReceipt.id
        )
        ocrService.extractItems(fileName,output[3])
        val json: String = gson.toJson(ocrOutput)
        return ResponseEntity.ok().body(json)
    }
}