package app.ocr_backend.receipt

import app.ocr_backend.household.Household
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface ReceiptRepository : JpaRepository<Receipt, Long>
{
    fun getReceiptById(receiptId:Long):Optional<Receipt>
    fun getByHousehold(household: Household): List<Receipt>
}
