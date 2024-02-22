package app.ocr_backend.controller

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import app.ocr_backend.repository.ReceiptCollectionRepository
import jakarta.annotation.PostConstruct
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/receipt")
class ReceiptController(val repository:ReceiptCollectionRepository) {

    @GetMapping("")
    fun getAllReceipts() = repository.receipts

    @GetMapping("/{id}")
    fun getReceiptById(@PathVariable id: Long): Receipt = repository.getReceiptById(id).orElseThrow{
        ResponseStatusException(HttpStatus.NOT_FOUND,"Receipt with the $id Id not exists")
    }

    @GetMapping("/{receiptId}/item/{itemId}")
    fun getItemById(
        @PathVariable receiptId:Long,
        @PathVariable itemId:Long
    ): Item =
        repository.getItemById(receiptId,itemId).orElseThrow{
        ResponseStatusException(HttpStatus.NOT_FOUND,"Item with the $itemId Id not exists int the Receipt with $receiptId Id")
    }

    @PostMapping("")
    fun createReceipt(@RequestBody receipt:Receipt)
    {
        repository.saveReceipt(receipt)
    }

    @DeleteMapping("")
    fun deleteReceipt(@RequestBody receiptId: Long)
    {
        repository.deleteReceipt(receiptId)
    }

    //TODO Test addItemToReceipt /{receiptId}/item
    @PostMapping("/{receiptId}/item")
    fun addItemToReceipt(@PathVariable receiptId:Long,@RequestBody item:Item)
    {
        repository.addItemToReceipt(receiptId,item)
    }


}