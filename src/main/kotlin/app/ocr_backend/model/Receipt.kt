package app.ocr_backend.model

import jakarta.persistence.*
import java.io.File
import java.util.Date

@Entity
data class Receipt(
    var dateOfPurchase:Date,
    var description:String,
    @Id @GeneratedValue var id: Long? = null,
) {

   @OneToMany(mappedBy="id")
    var items = ArrayList<Item>()
    var receiptImage: File? = null
    val totalCost:Int
        get(){
            var cost = 0
            for(item in items)
            {
                cost+=item.totalCost
            }
            return cost
        }
}