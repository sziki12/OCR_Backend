package app.ocr_backend.model

import app.ocr_backend.dto.ItemDTO
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "Item")
data class Item(
    var name:String,
    var quantity:Int,
    @Column(name = "total_cost")
    var totalCost:Int,
    )
{
    @Column(name="item_id")
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    var id: Long = -1

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="receipt_id")
    lateinit var receipt:Receipt


    constructor(itemId:Long,itemData: ItemDTO): this(itemData)
    {
        this.id = itemId
    }

    constructor(itemData: ItemDTO):
            this(itemData.name,itemData.quantity,itemData.totalCost)

    override fun equals(other: Any?): Boolean {

        val otherItem = other as? Item
        otherItem?.let {
            if(it.id==this.id)
                return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + quantity
        result = 31 * result + totalCost
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + receipt.hashCode()
        return result
    }
}