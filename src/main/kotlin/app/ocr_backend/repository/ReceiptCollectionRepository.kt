package app.ocr_backend.repository

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.collections.ArrayList

@Repository
class ReceiptCollectionRepository {
    val receipts = ArrayList<Receipt>()

    fun saveReceipt(receipt: Receipt)
    {
        receipts.add(receipt)
    }

    fun deleteReceipt(receiptId:Long)
    {
        val toRemove = receipts.firstOrNull {
            it.id == receiptId
        }

        toRemove.let {
            receipts.remove(it)
        }
    }

    fun getReceiptById(receiptId:Long):Optional<Receipt>
    {
        return receipts.stream().filter {
            it.id == receiptId
        }.findFirst()

    }

    fun getItemById(receiptId:Long,itemId:Long):Optional<Item>
    {
        return getReceiptById(receiptId).get().items.stream().filter {
            it.id == itemId
        }.findFirst()
    }

    fun addItemToReceipt(receiptId:Long,item:Item)
    {
        val receipt = getReceiptById(receiptId).get()
        receipt.items.add(item)
    }

    @PostConstruct
    fun init()
    {
        val i1 = Item("Alma",10,1500,1)
        val i2 = Item("GamePass",1,3000,2)
        val r1 = Receipt(
            Date(),
            "First Receipt",
            1)
        r1.items.addAll(listOf(i1,i2))
        val r2 = Receipt(
            Date(),
            "Second Receipt",
            2)
        receipts.addAll(listOf(r1,r2))
    }
}