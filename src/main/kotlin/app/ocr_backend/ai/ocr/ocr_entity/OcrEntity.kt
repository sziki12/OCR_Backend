package app.ocr_backend.ai.ocr.ocr_entity

import app.ocr_backend.ai.ocr.backend_dto.ProcessedReceipt
import app.ocr_backend.ai.ocr.frontend_dto.OcrResponse
import app.ocr_backend.receipt.Receipt
import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.Gson
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "ocr_response")
data class OcrEntity(
    @Column(name = "receipt_text", columnDefinition = "TEXT")
    var receiptText: String,
    @Column(name = "processed_receipt", columnDefinition = "TEXT")
    var processedReceipt: String,
    var date: LocalDate,
) {

    companion object {
        private val gson = Gson()
        /*@JsonIgnore
        val mainSeparator = "======"
        @JsonIgnore
        val itemSeparator = "=-----="*/

        fun fromOcrResponse(ocrResponse: OcrResponse, processedDate: LocalDate): OcrEntity {
            return OcrEntity(
                receiptText = ocrResponse.receiptText,
                processedReceipt = gson.toJson(ocrResponse.processedReceipt),
                date = processedDate
            )
        }

        private fun stringFromList(list: List<String>, separator: String): String {
            var out = ""
            for (item in list) {
                out += item + separator
            }
            return out
        }
    }

    @Column(name = "response_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "receipt_id")
    lateinit var receipt: Receipt

    fun toOcrResponse(): OcrResponse {
        return OcrResponse(
            receiptText = receiptText,
            processedReceipt = gson.fromJson(processedReceipt, ProcessedReceipt::class.java),
            date = receipt.dateOfPurchase.toString(),
            newReceiptId = receipt.id
        )
    }
}


