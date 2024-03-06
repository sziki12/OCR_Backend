package app.ocr_backend.controller

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import app.ocr_backend.repository.ReceiptCollectionRepository
import jakarta.annotation.PostConstruct
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.awt.Image
import javax.imageio.stream.ImageInputStream
import javax.imageio.stream.ImageInputStreamImpl

@RestController
@RequestMapping("/api/receipt")
@CrossOrigin
class ReceiptController(val repository:ReceiptCollectionRepository) {

    val modelController = ModelController()

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

    @ResponseStatus(HttpStatus.CREATED)
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
        repository.deleteItemFromReceipt(receiptId,itemId)
    }

    //UPDATE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{receiptId}")
    fun updateReceipt(@PathVariable receiptId: Long, @RequestBody receipt:Receipt)
    {
       repository.updateReceipt(receiptId,receipt)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{receiptId}/item/{itemId}")
    fun updateItem(@PathVariable receiptId: Long,@PathVariable itemId: Long, @RequestBody item:Item)
    {
        repository.updateItem(receiptId,itemId,item)
    }


    @PostMapping("/image")
    fun uploadImage(@RequestParam("file") image: MultipartFile): ResponseEntity<String> {
        //TODO receive and save image
        val output = modelController.processImage("reserved01.jpg")
        return ResponseEntity.ok().body(output)
    }
}