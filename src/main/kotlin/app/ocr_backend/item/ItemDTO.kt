package app.ocr_backend.item


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