package app.ocr_backend.service

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import app.ocr_backend.repository.ReceiptDBRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReceiptService(val receiptDBRepository: ReceiptDBRepository) {

    fun saveReceipt(receipt: Receipt): Receipt {
        println("saveReceipt: $receipt")
        val receiptToSave = Receipt()
        receiptToSave.let {
            it.description = receipt.description
            //it.dateOfPurchase = receipt.dateOfPurchase
            it.items = receipt.items
        }
        receiptDBRepository.save(receipt)
        return receipt
    }
    fun getReceipt(receiptId:Long): Optional<Receipt> {
        return receiptDBRepository.getReceiptById(receiptId)
    }

    fun updateReceipt(receipt: Receipt)
    {
        println("id: ${receipt.id}")
        val receiptToUpdate = receiptDBRepository.getReceiptById(receipt.id)
        if(receiptToUpdate.isPresent)
        {
            receiptToUpdate.get().let {
                it.description = receipt.description
                it.dateOfPurchase = receipt.dateOfPurchase
                it.items = receipt.items
            }
            receiptDBRepository.save(receiptToUpdate.get())
            println("FOUND AND UPDATED")
        }
        else
            println("NOT FOUND")
    }

    fun deleteReceipt(itemId:Long)
    {
        receiptDBRepository.deleteById(itemId)
    }

    fun getAllReceipt():List<Receipt>
    {
        return receiptDBRepository.findAll()
    }

}