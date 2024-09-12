package app.ocr_backend.ai.ocr.response

import java.time.LocalDate

data class OcrResponse(
    val extractedOcrResponse: ExtractedOcrResponse?,
    var plainText:List<String>,
    var filteredReceipt:List<String>,
    var extractedItems:List<String>,
    var date: String,
    var newReceiptId:Long,
)