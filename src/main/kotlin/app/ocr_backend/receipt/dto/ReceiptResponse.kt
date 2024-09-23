package app.ocr_backend.receipt.dto

import app.ocr_backend.item.Item
import app.ocr_backend.item.dto.ReceiptItemResponse
import app.ocr_backend.item.toItem
import app.ocr_backend.receipt.Receipt
import java.time.LocalDate

data class ReceiptResponse(
    var dateOfPurchase: LocalDate,
    var name:String,
    var items: List<ReceiptItemResponse>,
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
        private fun dtoFromItems(items:MutableList<Item>):MutableList<ReceiptItemResponse>
        {
            val list =  ArrayList<ReceiptItemResponse>()

            for(item in items)
            {
                list.add(ReceiptItemResponse(item))
            }

            return list
        }
    }

    fun toItemsArray():MutableList<Item>
    {
        val list =  ArrayList<Item>()

        for(itemDto in this.items)
        {
            list.add(itemDto.toItem())
        }

        return list
    }

    fun toReceipt(): Receipt {
        return Receipt(this)
    }
}