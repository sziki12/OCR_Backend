package app.ocr_backend.controller

import app.ocr_backend.dto.ReceiptDTO
import app.ocr_backend.model.Receipt
import app.ocr_backend.service.DBService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/receipt")
@CrossOrigin
class ReceiptController(private val service: DBService) {

    @GetMapping("")
    fun getAllReceipts(): List<Receipt> = service.getAllReceipt()

    @GetMapping("/{receiptId}")
    fun getReceiptById(@PathVariable receiptId: Long): Receipt = service.getReceipt(receiptId).orElseThrow{
        ResponseStatusException(HttpStatus.NOT_FOUND,"Receipt with the $receiptId Id not exists")
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    fun createReceipt(@RequestBody receiptData: ReceiptDTO)
    {
        service.saveReceipt(Receipt(receiptData))
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{receiptId}")
    fun deleteReceipt(@PathVariable receiptId: Long)
    {
        service.deleteReceipt(receiptId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{receiptId}")
    fun updateReceipt(@PathVariable receiptId: Long, @RequestBody receiptData: ReceiptDTO)
    {
        service.updateReceipt(Receipt(receiptId,receiptData))
    }
}