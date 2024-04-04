package app.ocr_backend.service

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import app.ocr_backend.repository.ReceiptDBRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReceiptService(val receiptDBRepository: ReceiptDBRepository) {

    fun saveReceipt(receipt: Receipt): Receipt {
        receiptDBRepository.save(receipt)
        return receipt
    }
    fun getReceipt(receiptId:Long): Optional<Receipt> {
        return receiptDBRepository.getReceiptById(receiptId)
    }

    fun updateReceipt(receipt: Receipt)
    {
        val receiptToUpdate = receiptDBRepository.getReceiptById(receipt.id)
        if(receiptToUpdate.isPresent)
        {
            receiptToUpdate.get().let {
                it.description = receipt.description
                it.dateOfPurchase = receipt.dateOfPurchase
                it.items = receipt.items
                it.isPending = receipt.isPending
            }
            receiptDBRepository.save(receiptToUpdate.get())
        }
    }

    fun deleteReceipt(itemId:Long)
    {
        receiptDBRepository.deleteById(itemId)
    }

    fun getAllReceipt():List<Receipt>
    {
        //TODO Assign Users to Receipts and return only the appropriate receipts
        return receiptDBRepository.findAll()
    }

}