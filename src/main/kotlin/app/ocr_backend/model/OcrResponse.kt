package app.ocr_backend.model

data class OcrResponse(
    var plainText:List<String>,
    var filteredReceipt:List<String>,
    var extractedItems:List<String>,
    var newReceiptId:Long,
)
