package app.ocr_backend.controller

import app.ocr_backend.dto.LlamaItemList
import app.ocr_backend.dto.OcrResponse
import app.ocr_backend.model.Receipt
import app.ocr_backend.service.DBService
import app.ocr_backend.service.ImageProcessingService
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
class ImageProcessingController(
    private val imageProcessingService:ImageProcessingService) {

    @PostMapping("/image")
    fun uploadImage(@RequestParam("file") image: MultipartFile): ResponseEntity<String> {

        val response = imageProcessingService.processImage(image)
        return ResponseEntity.ok().body(response)
    }
}