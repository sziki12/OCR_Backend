package app.ocr_backend.model

import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper
import jakarta.persistence.*
import java.io.File
import java.util.Date

@Entity
@Table(name = "receipt")
data class Receipt(
    var dateOfPurchase:Date,
    var description:String,
    @Id @GeneratedValue var id: Long? = null,
) {

   @OneToMany(targetEntity = receipt)
    var items = ArrayList<Item>()
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