package app.ocr_backend.place.dto

data class ReceiptResponsePlace(
    val id:Long,
    val name:String,
    val lat:Double,
    val lng:Double,
    val isValidated: Boolean,
)
