package app.ocr_backend.item

import app.ocr_backend.model.Item
import app.ocr_backend.receipt.Receipt
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ItemDBRepository:JpaRepository<Item,Long> {
    fun getItemById(itemId:Long): Optional<Item>

    fun deleteAllByReceipt(receipt: Receipt)
}