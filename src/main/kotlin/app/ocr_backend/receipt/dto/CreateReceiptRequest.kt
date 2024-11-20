package app.ocr_backend.receipt.dto

import app.ocr_backend.item.dto.ReceiptResponseItem
import java.time.LocalDate

data class CreateReceiptRequest(
    var dateOfPurchase: LocalDate,
    var name: String,
    var items: List<ReceiptResponseItem>,
    var isPending: Boolean,
)
