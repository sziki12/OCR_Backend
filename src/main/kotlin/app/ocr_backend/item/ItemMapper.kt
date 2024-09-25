package app.ocr_backend.item

import app.ocr_backend.item.dto.ReceiptResponseItem
import enumeration.Category

fun Item.toResponse() = ReceiptResponseItem(
    id = this.id,
    name = this.name,
    quantity = this.quantity,
    totalCost = this.totalCost,
    category = this.category.name,
)

fun ReceiptResponseItem.toItem() = Item(
    name = this.name,
    quantity = this.quantity,
    totalCost = this.totalCost,
).also {
    it.id = this.id
    it.category = Category.parse(this.category)
}