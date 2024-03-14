package app.ocr_backend.repository

import app.ocr_backend.model.Receipt
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ReceiptAndItemConnector(val receiptRepository: ReceiptDBRepository,val itemRepository: ItemDBRepository) {

    @Transactional
    fun updateReceipt(receiptId:Long, receipt: Receipt)
    {
        val targetReceipt = receiptRepository.findById(receiptId).get()
        targetReceipt.description = receipt.description
        targetReceipt.items = receipt.items
        targetReceipt.dateOfPurchase = receipt.dateOfPurchase
        //TODO Consistency of Items
    }

}