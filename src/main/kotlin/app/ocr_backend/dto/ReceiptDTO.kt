package app.ocr_backend.dto

import app.ocr_backend.model.Item
import java.util.*

data class ReceiptDTO(
    var dateOfPurchase: Date,
    var description:String,
    var items:MutableList<Item>,
)