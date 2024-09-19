package app.ocr_backend.receipt

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface ReceiptDBRepository : JpaRepository<Receipt, Long>
{
    fun getReceiptById(receiptId:Long):Optional<Receipt>
}
