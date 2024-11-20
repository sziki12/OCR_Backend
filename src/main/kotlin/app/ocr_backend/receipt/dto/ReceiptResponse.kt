package app.ocr_backend.receipt.dto

import app.ocr_backend.ai.ocr.ocr_entity.OcrEntity
import app.ocr_backend.item.dto.ReceiptResponseItem
import app.ocr_backend.place.dto.ReceiptResponsePlace
import app.ocr_backend.receipt_image.ReceiptImage
import app.ocr_backend.receipt_image.dto.ReceiptImageResponse
import java.time.LocalDate

data class ReceiptResponse(
    var id: Long,
    var dateOfPurchase: LocalDate,
    var totalCost: Int,
    var name: String,
    var items: List<ReceiptResponseItem>,
    var isPending: Boolean,
    var place: ReceiptResponsePlace?,
    var images: List<ReceiptImageResponse>,
    var ocrEntity: OcrEntity?,
)