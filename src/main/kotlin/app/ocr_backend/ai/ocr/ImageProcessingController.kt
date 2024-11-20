package app.ocr_backend.ai.ocr

import app.ocr_backend.ai.ocr.backend_dto.OcrParams
import app.ocr_backend.ai.ocr.frontend_dto.OcrResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/household/{householdId}")
@CrossOrigin
class ImageProcessingController(
    private val imageProcessingService: ImageProcessingService
) {

    @PostMapping("/image")
    fun uploadImage(
        @RequestParam("file") image: MultipartFile, @RequestParam ocrType: String,
        @RequestParam orientation: String, @RequestParam parseModel: String, @PathVariable householdId: UUID
    ): ResponseEntity<OcrResponse> {

        val ocrParams = OcrParams(
            ocr_type = ocrType,
            orientation = orientation,
            parse_model = parseModel,
            image = "",
            path = "",
        )
        val response = imageProcessingService.processImage(householdId, image, ocrParams)
        return ResponseEntity.ok().body(response)
    }
}