package app.ocr_backend.controller

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import app.ocr_backend.repository.ReceiptCollectionRepository
import jakarta.annotation.PostConstruct
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/receipt")
@CrossOrigin
class ReceiptController(val repository:ReceiptCollectionRepository) {

    //GET
    @GetMapping("")
    fun getAllReceipts() = repository.receipts

    @GetMapping("/{receiptId}")
    fun getReceiptById(@PathVariable receiptId: Long): Receipt = repository.getReceiptById(receiptId).orElseThrow{
        ResponseStatusException(HttpStatus.NOT_FOUND,"Receipt with the $receiptId Id not exists")
    }

    @GetMapping("/{receiptId}/item/{itemId}")
    fun getItemById(
        @PathVariable receiptId:Long,
        @PathVariable itemId:Long
    ): Item =
        repository.getItemById(receiptId,itemId).orElseThrow{
        ResponseStatusException(HttpStatus.NOT_FOUND,"Item with the $itemId Id not exists int the Receipt with $receiptId Id")
    }
    //CREATE
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    fun createReceipt(@RequestBody receipt:Receipt)
    {
        repository.saveReceipt(receipt)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{receiptId}/item")
    fun addItemToReceipt(@PathVariable receiptId:Long,@RequestBody item:Item)
    {
        repository.addItemToReceipt(receiptId,item)
    }

    //DELETE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{receiptId}")
    fun deleteReceipt(@PathVariable receiptId: Long)
    {
        repository.deleteReceipt(receiptId)
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{receiptId}/item/{itemId}")
    fun deleteItemFromReceipt(@PathVariable receiptId: Long, @PathVariable itemId: Long)
    {
        //TODO TEST DELETE
        repository.deleteItemFromReceipt(receiptId,itemId)
    }

    //UPDATE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{receiptId}")
    fun updateReceipt(@PathVariable receiptId: Long, @RequestBody receipt:Receipt)
    {
       repository.updateReceipt(receiptId,receipt)
    }


    //TODO Update Item
}