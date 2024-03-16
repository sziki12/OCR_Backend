package app.ocr_backend.service

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import app.ocr_backend.repository.ItemDBRepository
import app.ocr_backend.repository.ReceiptDBRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ItemService(val itemRepository: ItemDBRepository) {

    fun createNewItem(receipt: Receipt)
    {
        val newItem  = Item("",0,1)
        newItem.receipt = receipt
        receipt.items.add(newItem)
        itemRepository.save(newItem)
    }
    fun saveItem(receipt: Receipt, item:Item)
    {
        println("SAVE ITEM")
        receipt.items.add(item)
        item.receipt = receipt
        println("ADDED")
        itemRepository.save(item)
        println("SAVED")
    }
    fun getItem(itemId:Long): Optional<Item> {
        return itemRepository.getItemById(itemId)
    }

    fun updateItem(item:Item)
    {
        val itemToUpdate = itemRepository.getItemById(item.id)
        if(itemToUpdate.isPresent)
        {
            itemToUpdate.get().let {
                it.name = item.name
                it.quantity = item.quantity
                it.totalCost = item.totalCost
            }
            itemRepository.save(itemToUpdate.get())
        }
    }

    fun deleteItem(itemId:Long)
    {
        itemRepository.deleteById(itemId)
    }
}
