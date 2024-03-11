package app.ocr_backend.model

data class OcrResponse(
    var plainText:String,
    var filteredReceipt:String,
    var extractedItems:List<String>,
    var newReceiptId:Long,
)
