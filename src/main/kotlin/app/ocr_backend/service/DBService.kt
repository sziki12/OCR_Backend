package app.ocr_backend.service

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import app.ocr_backend.repository.ItemDBRepository
import app.ocr_backend.repository.ReceiptDBRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DBService(
    private val receiptService: ReceiptService,
    private val itemService: ItemService
) {
    fun createNewItem(receiptId: Long)
    {
        val receipt = receiptService.getReceipt(receiptId)
        if(receipt.isPresent)
            itemService.createNewItem(receipt.get())
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
}