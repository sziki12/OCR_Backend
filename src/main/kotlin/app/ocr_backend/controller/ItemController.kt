package app.ocr_backend.controller

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import app.ocr_backend.repository.ItemDBRepository
import app.ocr_backend.repository.ReceiptDBRepository
import com.google.gson.Gson
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/receipt")
@CrossOrigin
class ItemController(val itemRepository: ItemDBRepository) {

    val gson = Gson()
    @GetMapping("/{receiptId}/item/{itemId}")
    fun getItemById(
        @PathVariable receiptId:Long,
        @PathVariable itemId:Long
    ): Item =
        itemRepository.getItemById(receiptId,itemId).orElseThrow{
            ResponseStatusException(HttpStatus.NOT_FOUND,"Item with the $itemId Id not exists int the Receipt with $receiptId Id")
        }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{receiptId}/item")
    fun addItemToReceipt(@PathVariable receiptId:Long,@RequestBody item:Item)
    {
        itemRepository.saveItem(receiptId,item)
    }

    @PostMapping("/{receiptId}/new/item")
    fun addItemToReceipt(@PathVariable receiptId:Long): ResponseEntity<String> {
        val newItem = itemRepository.createNewItem(receiptId)
        val json: String = gson.toJson(newItem)
        return ResponseEntity.ok().body(json)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{receiptId}/item/{itemId}")
    fun deleteItemFromReceipt(@PathVariable receiptId: Long, @PathVariable itemId: Long)
    {
        itemRepository.deleteItem(receiptId,itemId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{receiptId}/item/{itemId}")
    fun updateItem(@PathVariable receiptId: Long,@PathVariable itemId: Long, @RequestBody item:Item)
    {
        itemRepository.updateItem(receiptId,itemId,item)
    }
}