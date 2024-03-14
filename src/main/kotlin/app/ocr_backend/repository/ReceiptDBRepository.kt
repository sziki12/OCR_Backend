package app.ocr_backend.repository

import app.ocr_backend.model.Item
import app.ocr_backend.model.Receipt
import jakarta.persistence.NamedQueries
import jakarta.persistence.NamedQuery
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.ListCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Repository
interface ReceiptDBRepository : JpaRepository<Receipt, Long>
{

}
