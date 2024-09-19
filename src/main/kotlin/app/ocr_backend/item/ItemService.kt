package app.ocr_backend.item

import app.ocr_backend.receipt.Receipt
import org.springframework.stereotype.Service
import java.util.*

@Service
class ItemService(val itemRepository: ItemDBRepository) {

    fun createNewItem(receipt: Receipt): Item {
        val newItem  = Item("",1,0)
        newItem.receipt = receipt
        itemRepository.save(newItem)
        return newItem
    }
    fun saveItem(receipt: Receipt, item: Item)
    {
        item.receipt = receipt
        itemRepository.save(item)
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

    fun deleteAllByReceipt(receipt: Receipt)
    {
        itemRepository.deleteAllByReceipt(receipt)
    }
}
