package app.ocr_backend.ai.llama

import app.ocr_backend.item.Item

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
    fun toItem(): Item
    {
        return Item(name,quantity,cost)
    }
}
