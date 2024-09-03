package app.ocr_backend.receipt

import app.ocr_backend.item.ItemDTO
import app.ocr_backend.model.Item
import java.time.LocalDate
import kotlin.collections.ArrayList

data class ReceiptDTO(
    var dateOfPurchase: LocalDate,
    var name:String,
    var items:MutableList<ItemDTO>,
    var isPending:Boolean,
)
{
    constructor(receipt: Receipt):this(
        receipt.dateOfPurchase,
        receipt.name,
        dtoFromItems(receipt.items),
        receipt.isPending
        )

    companion object{
        private fun dtoFromItems(items:MutableList<Item>):MutableList<ItemDTO>
        {
            val list =  ArrayList<ItemDTO>()

            for(item in items)
            {
                list.add(ItemDTO(item))
            }

            return list
        }
    }

    fun toItemsArray():MutableList<Item>
    {
        val list =  ArrayList<Item>()

        for(itemDto in this.items)
        {
            list.add(Item(itemDto))
        }

        return list
    }

    fun toReceipt(): Receipt {
        return Receipt(this)
    }
}