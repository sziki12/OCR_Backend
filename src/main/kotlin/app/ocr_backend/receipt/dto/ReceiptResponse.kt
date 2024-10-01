package app.ocr_backend.receipt.dto

import app.ocr_backend.item.dto.ReceiptResponseItem
import app.ocr_backend.place.dto.ReceiptResponsePlace
import java.time.LocalDate

data class ReceiptResponse(
    var id: Long,
    var dateOfPurchase: LocalDate,
    var totalCost: Int,
    var name: String,
    var items: List<ReceiptResponseItem>,
    var isPending: Boolean,
    var place: ReceiptResponsePlace?,
)