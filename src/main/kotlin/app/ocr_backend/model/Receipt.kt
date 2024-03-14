package app.ocr_backend.model

import jakarta.persistence.*
import java.io.File
import java.util.Date

@Entity
@Table(name = "receipts")
data class Receipt(
    @Column(name="date_of_purchase")
    var dateOfPurchase:Date,
    var description:String,
    @Column(name="receipt_id")
    @Id @GeneratedValue var id: Long? = null,
) {

   @OneToMany(mappedBy = "receipt")
   var items:MutableList<Item> = mutableListOf()
    var imageName: String? = null
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