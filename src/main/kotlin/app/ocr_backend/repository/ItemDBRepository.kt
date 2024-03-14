package app.ocr_backend.repository

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ItemDBRepository:JpaRepository<Item,Long> {

    @Query("SELECT Item FROM Item ")
    fun getItemById(receiptId:Long,itemId:Long): Optional<Item>
    {
        return TODO()
    }

    @Query("SELECT Item FROM Item ")
    fun saveItem(receiptId:Long,item:Item)
    {
        TODO()
    }

    @Query("SELECT Item FROM Item ")
    fun deleteItem(receiptId:Long,itemId:Long)
    {
        TODO()
    }

    @Query("SELECT Item FROM Item ")
    fun updateItem(receiptId:Long,itemId:Long,item:Item)
    {
        TODO()
    }

    @Query("SELECT Item FROM Item ")
    fun createNewItem(receiptId:Long): Optional<Item>
    {
        return TODO()
    }
}