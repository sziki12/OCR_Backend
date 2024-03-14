package app.ocr_backend.repository

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface ReceiptDBRepository : JpaRepository<Receipt, Long>
{
    //fun getReceiptById(id:Long): Optional<Receipt>

    fun getReceiptByIdAndItemsIdIn(id: Long, items_id: MutableCollection<Long>): Optional<Item>

}
