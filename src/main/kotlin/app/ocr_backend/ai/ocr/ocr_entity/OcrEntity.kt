package app.ocr_backend.ai.ocr.ocr_entity

import app.ocr_backend.ai.ocr.response.OcrResponse
import app.ocr_backend.receipt.Receipt
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name="ocr_response")
data class OcrEntity(
    @Column(name = "plain_text",columnDefinition="TEXT")
    var plainText:String,
    @Column(name = "filtered_receipt",columnDefinition="TEXT")
    var filteredReceipt:String,
    @Column(name = "extracted_items",columnDefinition="TEXT")
    var extractedItems:String,
    var date: LocalDate,
)
{
    companion object
    {
        @JsonIgnore
        val mainSeparator = "======"
        @JsonIgnore
        val itemSeparator = "=-----="

        fun fromOcrResponse(ocrResponse: OcrResponse): OcrEntity
        {
            return OcrEntity(
                plainText = stringFromList(ocrResponse.plainText,"\n"),
                filteredReceipt = stringFromList(ocrResponse.filteredReceipt,"\n"),
                extractedItems = stringFromList(ocrResponse.extractedItems, "\n"+ itemSeparator),
                date = LocalDate.now()
            )
        }
        private fun stringFromList(list:List<String>,separator:String):String
        {
            var out = ""
            for(item in list)
            {
                out+=item+separator
            }
            return out
        }
    }

    @Column(name="response_id")
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id:Long = -1

    @JsonIgnore
    @OneToOne
    @JoinColumn(name="receipt_id")
    lateinit var receipt: Receipt

    fun toOcrResponse(): OcrResponse
    {
        return OcrResponse(
            extractedOcrResponse = null,//TODO fix extractedOcrResponse = null
            plainText = plainText.split("\n"),
            filteredReceipt = filteredReceipt.split("\n"),
            extractedItems = extractedItems.split(itemSeparator),
            date = receipt.dateOfPurchase.toString(),
            newReceiptId = receipt.id
        )
    }
}


