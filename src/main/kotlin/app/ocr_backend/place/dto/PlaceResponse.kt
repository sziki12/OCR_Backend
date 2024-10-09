package app.ocr_backend.place.dto

import app.ocr_backend.receipt.dto.ReceiptResponse

data class PlaceResponse(
    val id:Long,
    val name:String,
    val description: String,
    val lat:Double,
    val lng:Double,
    val isValidated: Boolean,
    val receipts: List<ReceiptResponse>
)
