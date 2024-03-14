package app.ocr_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "items")
data class Item(
    var name:String,
    var quantity:Int,
    var totalCost:Int,
    @Column(name="item_id")
    @Id @GeneratedValue var id: Long? = null,
    )
{
    @ManyToOne
    @JoinColumn(name="receipt_id")
    lateinit var receipt:Receipt
}