package app.ocr_backend.model

import app.ocr_backend.dto.ItemDTO
import app.ocr_backend.dto.ReceiptDTO
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

    @Column(name="receipt_id")
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    var id: Long = -1

   @OneToMany(mappedBy = "receipt")
   var items:MutableList<Item> = mutableListOf()

    @OneToMany(mappedBy = "receipt")
    var images:MutableList<ReceiptImage> = mutableListOf()

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
}