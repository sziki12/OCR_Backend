package app.ocr_backend.dto

import jakarta.persistence.Column

data class ItemDTO(
    var id:Long?,
    var name:String,
    var quantity:Int,
    var totalCost:Int,
)