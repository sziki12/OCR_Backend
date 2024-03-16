package app.ocr_backend.repository

import app.ocr_backend.model.ReceiptImage
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ImageDBRepository:JpaRepository<ReceiptImage,Long> {
    fun getReceiptImageById(imageId:Long):Optional<ReceiptImage>

    fun getByReceiptId(receiptId:Long):List<ReceiptImage>
}