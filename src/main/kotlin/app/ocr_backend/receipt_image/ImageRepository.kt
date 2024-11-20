package app.ocr_backend.receipt_image

import app.ocr_backend.receipt.Receipt
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ImageRepository:JpaRepository<ReceiptImage,Long> {
    fun getReceiptImageById(imageId:Long):Optional<ReceiptImage>

    fun getByReceiptId(receiptId:Long):List<ReceiptImage>

    fun deleteAllByReceipt(receipt: Receipt)

}