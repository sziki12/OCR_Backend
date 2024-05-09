package app.ocr_backend.dto

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import java.time.LocalDate
import java.util.*

data class ReceiptDTO(
    var dateOfPurchase: LocalDate,
    var name:String,
    var items:MutableList<Item>,
    var isPending:Boolean,
)
{
    constructor(receipt:Receipt):this(
        receipt.dateOfPurchase,
        receipt.name,
        receipt.items,
        receipt.isPending
        )
}