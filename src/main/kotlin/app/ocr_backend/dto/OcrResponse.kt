package app.ocr_backend.dto

import java.time.LocalDate
import java.util.*

data class OcrResponse(
    var plainText:List<String>,
    var filteredReceipt:List<String>,
    var extractedItems:List<String>,
    var date: LocalDate,
    var newReceiptId:Long,
)