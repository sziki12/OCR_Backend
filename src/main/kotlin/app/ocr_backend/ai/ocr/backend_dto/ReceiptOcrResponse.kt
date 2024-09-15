package app.ocr_backend.ai.ocr.backend_dto

data class ReceiptOcrResponse(
    val processed_receipt: ProcessedReceipt,
    val receipt_text: String
)
