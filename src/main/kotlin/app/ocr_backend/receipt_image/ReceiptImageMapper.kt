package app.ocr_backend.receipt_image

import app.ocr_backend.receipt_image.dto.ReceiptImageResponse


fun ReceiptImage.toResponse() = ReceiptImageResponse(
    this.name,
    this.id
)