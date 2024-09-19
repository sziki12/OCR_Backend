package app.ocr_backend.ai.ocr.backend_dto

import app.ocr_backend.item.Item

data class ExtractedItem(
    val name: String,
    val quantity: Int,
    val price: Int
) {
    fun toItem(): Item {
        return Item(name = name, quantity = quantity, totalCost = price)
    }
}
