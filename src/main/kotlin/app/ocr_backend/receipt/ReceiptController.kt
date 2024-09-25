package app.ocr_backend.receipt

import app.ocr_backend.receipt.dto.CreateReceiptRequest
import app.ocr_backend.receipt.dto.ReceiptResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/household/{householdId}")
@CrossOrigin
class ReceiptController(
    private val receiptService: ReceiptService,
) {

    @GetMapping("/receipt")
    fun getAllReceipts(@PathVariable householdId: UUID): List<ReceiptResponse> {
        return receiptService.getReceiptsByHousehold(householdId).map { it.toResponse() }
    }

    @GetMapping("receipt/{receiptId}")
    fun getReceiptById(@PathVariable receiptId: Long, @PathVariable householdId: UUID): ReceiptResponse =
        receiptService.getReceipt(householdId, receiptId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Receipt with the $receiptId Id not exists")
        }.toResponse()

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("receipt")
    fun createReceipt(@RequestBody createRequest: CreateReceiptRequest, @PathVariable householdId: UUID) {
        receiptService.saveReceipt(householdId, createRequest.toReceipt())
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("receipt/{receiptId}")
    fun deleteReceipt(@PathVariable receiptId: Long, @PathVariable householdId: UUID) {
        receiptService.deleteReceipt(householdId, receiptId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("receipt")
    fun updateReceipt(
        @RequestBody receiptData: ReceiptResponse,
        @PathVariable householdId: UUID
    ) {
        val receipts = receiptService.getReceiptsByPlace(householdId, receiptData.place?.id ?: -1)
        receiptService.updateReceipt(householdId, receiptData.toReceipt(receipts))
    }
}