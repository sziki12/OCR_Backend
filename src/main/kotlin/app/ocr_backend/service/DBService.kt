package app.ocr_backend.service

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import app.ocr_backend.model.ReceiptImage
import app.ocr_backend.repository.ItemDBRepository
import app.ocr_backend.repository.ReceiptDBRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class DBService(
    private val receiptService: ReceiptService,
    private val itemService: ItemService,
    private val imageService: ImageService
) {

    //ITEM
    fun createNewItem(receiptId: Long): Item? {
        val receipt = receiptService.getReceipt(receiptId)
        if(receipt.isPresent)
            return itemService.createNewItem(receipt.get())
        return null
    }

    fun saveItem(receiptId: Long, item: Item)
    {
        val receipt = receiptService.getReceipt(receiptId)
        if(receipt.isPresent)
            itemService.saveItem(receipt.get(),item)
    }
    fun getItem(itemId:Long): Optional<Item> {
        return itemService.getItem(itemId)
    }

    fun updateItem(item: Item)
    {
        itemService.updateItem(item)
    }

    fun deleteItem(itemId:Long)
    {
        itemService.deleteItem(itemId)
    }

    //RECEIPT

    fun saveReceipt(receipt: Receipt): Receipt {
        return receiptService.saveReceipt(receipt)
    }
    fun getReceipt(receiptId:Long): Optional<Receipt> {
        return receiptService.getReceipt(receiptId)
    }

    fun updateReceipt(receipt: Receipt)
    {
        /*println()
        println("RECEIPT: $receipt")
        println()*/
        for(item in receipt.items)
            itemService.updateItem(item)
        receiptService.updateReceipt(receipt)
    }

    @Transactional
    fun deleteReceipt(receiptId:Long)
    {
        val receipt = receiptService.getReceipt(receiptId)
        if(receipt.isPresent)
        {
            imageService.deleteAllByReceipt(receipt.get())
            itemService.deleteAllByReceipt(receipt.get())
            receiptService.deleteReceipt(receiptId)
        }

    }

    fun getAllReceipt():List<Receipt>
    {
        return receiptService.getAllReceipt()
    }

    //IMAGE
    fun getImages(receiptId:Long): List<ReceiptImage> {
        return imageService.getImages(receiptId)
    }

    fun saveImage(receiptId: Long,imageName:String)
    {
        val receipt = receiptService.getReceipt(receiptId)
        if(receipt.isPresent)
            imageService.saveImage(receipt.get(),imageName)
    }

    fun deleteImage(imageId:Long)
    {
        return imageService.deleteImage(imageId)
    }

    fun generateImageName(receipt: Receipt,image: MultipartFile):String
    {
        return imageService.generateImageName(receipt,image)
    }
}