package app.ocr_backend.receipt

import app.ocr_backend.item.toResponse
import app.ocr_backend.receipt.dto.ReceiptResponse

fun Receipt.toResponse() = ReceiptResponse(
    name = this.name,
    dateOfPurchase = this.dateOfPurchase,
    isPending = this.isPending,
    items = this.items.map { it.toResponse() }
)