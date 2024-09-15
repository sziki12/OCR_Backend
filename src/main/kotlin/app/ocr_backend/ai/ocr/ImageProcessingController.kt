package app.ocr_backend.ai.ocr

import app.ocr_backend.ai.ocr.frontend_dto.OcrResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api")
@CrossOrigin
class ImageProcessingController(
    private val imageProcessingService: ImageProcessingService
) {

    @PostMapping("/image")
    fun uploadImage(@RequestParam("file") image: MultipartFile,  ): ResponseEntity<OcrResponse> {

        val response = imageProcessingService.processImage(image)
        return ResponseEntity.ok().body(response)
    }
}