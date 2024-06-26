package app.ocr_backend.controller

import app.ocr_backend.dto.OcrResponse
import app.ocr_backend.service.DBService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/ocr")
@CrossOrigin
class OcrEntityController(val service:DBService) {

    @GetMapping("/response/{receiptId}")
    fun getOcrResponse(@PathVariable receiptId:Long): OcrResponse {
        return service.getOcrResponse(receiptId).orElseThrow{
            ResponseStatusException(HttpStatus.NOT_FOUND,"OcrResponse not exists for receipt with id: $receiptId")
        }
    }
}