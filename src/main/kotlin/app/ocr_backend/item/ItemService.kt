package app.ocr_backend.item

import app.ocr_backend.receipt.Receipt
import app.ocr_backend.receipt.ReceiptService
import org.springframework.stereotype.Service
import java.util.*

@Service
class ItemService(
    val itemRepository: ItemRepository,
    val receiptService: ReceiptService,
    ) {

    fun createNewItem(receiptId: Long): Optional<Item> {
        val receipt = receiptService.getReceipt(receiptId)
        if(receipt.isPresent){
            val newItem  = Item("",1,0)
            newItem.receipt = receipt.get()
            itemRepository.save(newItem)
        }
        return Optional.empty()
    }
    fun saveItem(receiptId: Long, item: Item): Optional<Item>
    {   val receipt = receiptService.getReceipt(receiptId)
        if(receipt.isPresent){
            item.receipt = receipt.get()
            return Optional.of(itemRepository.save(item))
        }
        return Optional.empty()
    }
    fun getItem(itemId:Long): Optional<Item> {
        return itemRepository.getItemById(itemId)
    }

    fun updateItem(item: Item)
    {
        val itemToUpdate = itemRepository.getItemById(item.id)
        if(itemToUpdate.isPresent)
        {
            item.receipt = itemToUpdate.get().receipt
            itemRepository.save(item)
        }
    }

    fun deleteItem(itemId:Long)
    {
        itemRepository.deleteById(itemId)
    }

    fun deleteAllByReceipt(receiptId: Long)
    {
        val receipt = receiptService.getReceipt(receiptId)
        if(receipt.isPresent){
            itemRepository.deleteAllByReceipt(receipt.get())
        }
    }
}
