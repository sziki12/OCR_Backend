package app.ocr_backend.repository

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.PathVariable
import java.util.*
import kotlin.collections.ArrayList

@Repository
class ReceiptCollectionRepository {
    val receipts = ArrayList<Receipt>()

    fun saveReceipt(receipt: Receipt)
    {
        receipt.id = getNextReceiptId()
        receipt.items = ArrayList()
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

    fun deleteItemFromReceipt(receiptId: Long, itemId: Long)
    {
        val receipt = getReceiptById(receiptId).get()
        val item = receipt.items.firstOrNull {
            it.id == itemId
        }
        item?.let {
            receipt.items.remove(it)
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
        item.id = getNextItemId(receipt)
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
        val r3 = Receipt(
            Date(),
            "Third Receipt",
            3)
        val r4 = Receipt(
            Date(),
            "Fourth Receipt",
            4)
        receipts.addAll(listOf(r1,r2,r3,r4))
    }

    //TODO Disable Id Generation for Update
    private fun getNextReceiptId():Long
    {
        val receipt = receipts.maxBy {it.id?:0}
        val newId = receipt.id?.plus(1)?:0
        return newId
    }

    fun getNextItemId(receipt: Receipt):Long
    {
        val item = receipt.items.maxByOrNull { it.id?:0 }
        val newId = item?.id?.plus(1) ?: 0
        return newId
    }
}