package app.ocr_backend.controller

import app.ocr_backend.dto.OcrResponse
import app.ocr_backend.dto.ReceiptDTO
import app.ocr_backend.model.Receipt
import app.ocr_backend.service.ReceiptService
import app.ocr_backend.utils.PathHandler
import com.google.gson.Gson
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.File
import kotlin.io.path.pathString

@RestController
@RequestMapping("/api/receipt")
@CrossOrigin
class ReceiptController(val service: ReceiptService) {

    val modelController = ModelController()
    val gson = Gson()

    //GET
    @GetMapping("")
    fun getAllReceipts(): List<Receipt> = service.getAllReceipt()

    @GetMapping("/{receiptId}")
    fun getReceiptById(@PathVariable receiptId: Long): Receipt = service.getReceipt(receiptId).orElseThrow{
        ResponseStatusException(HttpStatus.NOT_FOUND,"Receipt with the $receiptId Id not exists")
    }

    //CREATE
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    fun createReceipt(@RequestBody receiptData: ReceiptDTO)
    {
        service.saveReceipt(Receipt(receiptData))
    }

    //DELETE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{receiptId}")
    fun deleteReceipt(@PathVariable receiptId: Long)
    {
        service.deleteReceipt(receiptId)
    }


    //UPDATE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{receiptId}")
    fun updateReceipt(@PathVariable receiptId: Long, @RequestBody receiptData: ReceiptDTO)
    {
        service.updateReceipt(Receipt(receiptId,receiptData))
    }

    @PostMapping("/image")
    fun uploadImage(@RequestParam("file") image: MultipartFile): ResponseEntity<String> {
        val altName = "file.jpg"
        val file = File(PathHandler.getImageDir().pathString + File.separator + (image.originalFilename?:altName))
        image.transferTo(file)


        val separator = "======"
        val itemSeparator = "------"
        val output = modelController.processImage(image.originalFilename?:altName).split(separator)

        val newReceiptId = service.saveReceipt(Receipt()).id
        val ocrOutput = newReceiptId?.let {
            OcrResponse(
                plainText = output[1].split("\n"),
                filteredReceipt = output[1].split("\n"),
                extractedItems = output[2].split(itemSeparator),
                it
            )
        }?:""

        val json: String = gson.toJson(ocrOutput)
        return ResponseEntity.ok().body(json)
    }
}