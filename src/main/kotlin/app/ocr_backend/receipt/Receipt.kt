package app.ocr_backend.receipt

import app.ocr_backend.ai.ocr.ocr_entity.OcrEntity
import app.ocr_backend.item.Item
import app.ocr_backend.place.Place
import app.ocr_backend.receipt_image.ReceiptImage
import app.ocr_backend.user.User
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "Receipt")
data class Receipt(
    @Column(name="date_of_purchase")
    var dateOfPurchase:LocalDate,
    var name:String,
) {

    @Column(name="is_pending")
    var isPending:Boolean = false

    @Column(name="receipt_id")
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    var id: Long = -1

   @OneToMany(mappedBy = "receipt")
   var items:MutableList<Item> = mutableListOf()

    @OneToMany(mappedBy = "receipt")
    var images:MutableList<ReceiptImage> = mutableListOf()

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    lateinit var user: User

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="place_id")
    var place: Place? = null

    @OneToOne(mappedBy = "receipt")
    var ocrEntity: OcrEntity? = null

    val placeName:String?
        get()=place?.name
    val totalCost:Int
        get(){
            var cost = 0
            for(item in items)
            {
                cost += item.totalCost
            }
            return cost
        }

    constructor():this(LocalDate.now(),"")
    constructor(receiptId:Long,receiptData: ReceiptDTO):this(receiptData)
    {
        this.id = receiptId
    }

    constructor(receiptData: ReceiptDTO):
            this(receiptData.dateOfPurchase,receiptData.name)
    {
        this.items = receiptData.toItemsArray()
    }

    override fun equals(other: Any?): Boolean {

        val otherReceipt = other as? Receipt
        otherReceipt?.let {
            if(it.id==this.id)
                return true
        }
        return false
    }

    override fun toString(): String {
        return "Receipt(id: $id, isPedding: $isPending, name: $name, dateOfPurchase: $dateOfPurchase, items: $items, place: $place)"
    }

    override fun hashCode(): Int {
        var result = dateOfPurchase.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + isPending.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + images.hashCode()
        return result
    }
}