package app.ocr_backend.model

import app.ocr_backend.dto.ItemDTO
import app.ocr_backend.dto.OcrResponse
import app.ocr_backend.dto.ReceiptDTO
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.io.File
import java.util.Date

@Entity
@Table(name = "Receipt")
data class Receipt(
    @Column(name="date_of_purchase")
    var dateOfPurchase:Date,
    var description:String,
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
    lateinit var user:User
    //@Column(name="ocr_output")
    //var ocrOutput:OcrResponse? = null

    val totalCost:Int
        get(){
            var cost = 0
            for(item in items)
            {
                cost+=item.totalCost
            }
            return cost
        }

    constructor():this(Date(),"")
    constructor(receiptId:Long,receiptData: ReceiptDTO):this(receiptData)
    {
        this.id = receiptId
    }

    constructor(receiptData: ReceiptDTO):
            this(receiptData.dateOfPurchase,receiptData.description)
    {
        this.items = receiptData.items
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
        return super.toString()+", "+this.items+", id:"+this.id
    }

    override fun hashCode(): Int {
        var result = dateOfPurchase.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + isPending.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + images.hashCode()
        return result
    }
}