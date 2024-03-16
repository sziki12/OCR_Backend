package app.ocr_backend.repository

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ItemDBRepository:JpaRepository<Item,Long> {
    fun getItemById(itemId:Long): Optional<Item>
}