package app.ocr_backend.dto

import app.ocr_backend.model.Item
import enumeration.Category
import jakarta.persistence.Column


data class ItemDTO(
    var id:Long?,
    var name:String,
    var quantity:Int,
    var totalCost:Int,
    var category: String
){
    constructor(item: Item):this(
        item.id,
        item.name,
        item.quantity,
        item.totalCost,
        item.category.name)
}