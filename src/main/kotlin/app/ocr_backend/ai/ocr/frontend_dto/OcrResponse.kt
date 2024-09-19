package app.ocr_backend.ai.ocr.frontend_dto

import app.ocr_backend.ai.ocr.backend_dto.ProcessedReceipt


data class OcrResponse(
    var receiptText:String,
    var processedReceipt: ProcessedReceipt,
    var date: String,
    var newReceiptId:Long,
)