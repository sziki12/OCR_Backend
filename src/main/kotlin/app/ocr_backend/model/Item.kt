package app.ocr_backend.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class Item(
    var name:String,
    var quantity:Int,
    var totalCost:Int,
    @Id @GeneratedValue var id: Long? = null,
    )