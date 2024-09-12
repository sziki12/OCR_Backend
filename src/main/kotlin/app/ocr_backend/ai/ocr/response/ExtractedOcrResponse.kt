package app.ocr_backend.ai.ocr.response

import app.ocr_backend.item.Item

data class ExtractedOcrResponse(
    val store_name: String?,
    val store_address: String?,
    val total_cost: Int,
    val date_of_purchase: String?,
    val purchased_items: List<ExtractedItem>
) {
    fun toItemList(): List<Item> {
        val items = mutableListOf<Item>()
        for (purchasedItem in purchased_items) {
            items.add(purchasedItem.toItem())
        }
        return items
    }
}