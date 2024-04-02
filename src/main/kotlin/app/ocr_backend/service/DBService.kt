package app.ocr_backend.service

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import app.ocr_backend.model.ReceiptImage
import app.ocr_backend.model.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class DBService(
    private val receiptService: ReceiptService,
    private val itemService: ItemService,
    private val imageService: ImageService,
    private val userService: UserService
) {

    //ITEM
    fun createNewItem(receiptId: Long): Optional<Item> {
        val receipt = receiptService.getReceipt(receiptId)
        return if(receipt.isPresent)
             Optional.of(itemService.createNewItem(receipt.get()))
        else Optional.empty()
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

    fun saveReceipt(receipt: Receipt): Optional<Receipt> {
        val optUser = getCurrentUser()
        if(optUser.isPresent)
        {
            receipt.user = optUser.get()
            val savedReceipt = receiptService.saveReceipt(receipt)
            return Optional.of(savedReceipt)
        }
        return Optional.empty()
    }
    fun getReceipt(receiptId:Long): Optional<Receipt> {
       val optUser = getCurrentUser()
        return if(optUser.isPresent) {
            val user = optUser.get()
            user.receipts.find { it.user == user }?.let { Optional.of(it) } ?: Optional.empty<Receipt>()
        } else
            Optional.empty<Receipt>()
    }

    fun updateReceipt(receipt: Receipt)
    {
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
        val optUser = getCurrentUser()
        return if(optUser.isPresent)
            optUser.get().receipts
        else
            listOf()
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

    private fun getCurrentUser(): Optional<User> {
        val actualUserName = SecurityContextHolder.getContext().authentication.principal.toString()
        return userService.findByUserName(actualUserName)
    }
}