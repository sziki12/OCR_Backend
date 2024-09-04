package app.ocr_backend.receipt_image

import app.ocr_backend.receipt.Receipt
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*


@Entity
@Table(name="Image")
data class ReceiptImage(var name:String) {

    @Column(name="image_id")
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id: Long = -1

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="receipt_id")
    lateinit var receipt: Receipt
}