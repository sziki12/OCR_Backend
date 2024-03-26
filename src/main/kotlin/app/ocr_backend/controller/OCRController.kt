package app.ocr_backend.controller

import app.ocr_backend.dto.LlamaItemList
import app.ocr_backend.dto.OcrResponse
import app.ocr_backend.dto.ReceiptDTO
import app.ocr_backend.model.Receipt
import app.ocr_backend.service.DBService
import app.ocr_backend.service.OCRService
import app.ocr_backend.utils.PathHandler
import com.google.gson.Gson
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import kotlin.io.path.pathString

@RestController
@RequestMapping("/api")
@CrossOrigin
class OCRController(private val service: DBService) {
    private final val ocrService = OCRService()
    private val gson = Gson()

    private final val separator = "======"
    private final val itemSeparator = "------"

    @PostMapping("/image")
    fun uploadImage(@RequestParam("file") image: MultipartFile): ResponseEntity<String> {

        val newReceipt = service.saveReceipt(Receipt().also { it.isPending = true })
        val fileName = service.generateImageName(newReceipt,image)
        val file = File(PathHandler.getImageDir().pathString + File.separator + fileName)

        image.transferTo(file)

        val output = ocrService.processImage(fileName).split(separator)

        service.saveImage(newReceipt.id,fileName)

        val ocrOutput = OcrResponse(
            plainText = output[1].split("\n"),
            filteredReceipt = output[2].split("\n"),
            extractedItems = output[3].split(itemSeparator),
            newReceipt.id
        )

        val itemsJson = ocrService.extractItems(fileName,output[3])
        try {
            val llamaItemList = gson.fromJson(itemsJson, LlamaItemList::class.java)
            println(llamaItemList)
            for(item in llamaItemList.toItemList())
            {
                service.saveItem(newReceipt.id,item)
            }
        }
        catch (e:Exception)
        {
            e.printStackTrace()
            //Failed to read JSON
        }
        service.updateReceipt(newReceipt.also { it.isPending = false })
        val responseJson: String = gson.toJson(ocrOutput)
        return ResponseEntity.ok().body(responseJson)
    }

    fun extractTextUsingOcr()
    {

    }

    fun extractItemsUsingLlama()
    {

    }

    @GetMapping("/test")
    fun test()
    {
        val file = File("C:\\Users\\Szikszai Levente\\IdeaProjects\\OCR_Backend\\src\\main\\resources\\llama\\output_text\\Reserved0260.txt")
        val itemsJson = ocrService.extractJson(file)
        try {
            val llamaItemList = gson.fromJson(itemsJson, LlamaItemList::class.java)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
            //Failed to read JSON
        }
    }
}