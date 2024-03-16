package app.ocr_backend.model

import app.ocr_backend.dto.ItemDTO
import app.ocr_backend.dto.ReceiptDTO
import jakarta.persistence.*
import java.io.File
import java.util.Date

@Entity
@Table(name = "receipts")
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

    constructor(receiptId:Long,receiptData: ReceiptDTO):this(receiptData)
    {
        this.id = receiptId
    }

    constructor(receiptData: ReceiptDTO):
            this(receiptData.dateOfPurchase,receiptData.description)
    {
        this.items= receiptData.items
    }

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
}