package app.ocr_backend.dto

import app.ocr_backend.model.Item

data class LlamaItemList(
    val items:List<LlamaItem>
)
{
    fun toItemList():List<Item>
    {
        val list = ArrayList<Item>()
        for(item in items)
            list.add(item.toItem())
        return list
    }
}

data class LlamaItem(
    val name:String,
    val quantity:Int,
    val cost:Int,
)
{
    fun toItem():Item
    {
        return Item(name,quantity,cost)
    }
}
