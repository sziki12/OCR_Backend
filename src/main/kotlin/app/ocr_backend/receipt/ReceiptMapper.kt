package app.ocr_backend.receipt

import app.ocr_backend.item.toItem
import app.ocr_backend.item.toResponse
import app.ocr_backend.place.toPlace
import app.ocr_backend.place.toReceiptResponse
import app.ocr_backend.receipt.dto.CreateReceiptRequest
import app.ocr_backend.receipt.dto.ReceiptResponse

fun Receipt.toResponse() = ReceiptResponse(
    id = this.id,
    name = this.name,
    dateOfPurchase = this.dateOfPurchase,
    isPending = this.isPending,
    items = this.items.map { it.toResponse() },
    place = this.place?.toReceiptResponse()
)

fun ReceiptResponse.toReceipt(placeReceipts: List<Receipt>) = Receipt(
    name = this.name,
    dateOfPurchase = this.dateOfPurchase,
).also { receipt ->
    receipt.items.addAll(this.items.map { it.toItem() })
    receipt.isPending = this.isPending
    receipt.place = this.place?.toPlace(placeReceipts)
}

fun CreateReceiptRequest.toReceipt() = Receipt(
    name = this.name,
    dateOfPurchase = this.dateOfPurchase,
).also {
    it.items.addAll(this.items.map { it.toItem() })
    it.isPending = this.isPending
}