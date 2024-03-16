package app.ocr_backend.dto

import jakarta.persistence.Column

data class ItemDTO(
    var name:String,
    var quantity:Int,
    var totalCost:Int,
)