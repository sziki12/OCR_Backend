package app.ocr_backend.item.dto


data class ReceiptItemResponse(
    var id: Long,
    var name: String,
    var quantity: Int,
    var totalCost: Int,
    var category: String
)