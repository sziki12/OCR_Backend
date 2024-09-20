package app.ocr_backend.ai.ocr.ocr_entity

import app.ocr_backend.ai.ocr.frontend_dto.OcrResponse
import app.ocr_backend.db_service.DBService
import app.ocr_backend.receipt.ReceiptService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/ocr")
@CrossOrigin
class OcrEntityController(val receiptService: ReceiptService) {

    @GetMapping("/response/{receiptId}")
    fun getOcrResponse(@PathVariable receiptId:Long): OcrResponse {
        return receiptService.getOcrResponse(receiptId).orElseThrow{
            ResponseStatusException(HttpStatus.NOT_FOUND,"OcrResponse not exists for receipt with id: $receiptId")
        }
    }
}