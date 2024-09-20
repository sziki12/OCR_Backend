package app.ocr_backend.item

import app.ocr_backend.ai.llama.ItemCategorisingService
import com.google.gson.Gson
import enumeration.Category
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/receipt")
@CrossOrigin
class ItemController(
    private val itemService: ItemService,
    private val categorisingService: ItemCategorisingService
) {

    val gson = Gson()
    @GetMapping("/{receiptId}/item/{itemId}")
    fun getItemById(
        @PathVariable receiptId:Long,
        @PathVariable itemId:Long
    ): Item =
        itemService.getItem(itemId).orElseThrow{
            ResponseStatusException(HttpStatus.NOT_FOUND,"Item with the $itemId Id not exists int the Receipt with $receiptId Id")
        }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{receiptId}/item")
    fun addItemToReceipt(@PathVariable receiptId:Long,@RequestBody item: Item)
    {
        itemService.saveItem(receiptId,item)
    }

    @PostMapping("/{receiptId}/new/item")
    fun addItemToReceipt(@PathVariable receiptId:Long): ResponseEntity<String> {
        val newItem = itemService.createNewItem(receiptId)
        if(newItem.isPresent)
        {
            newItem.get().let {
                val json: String = gson.toJson(ItemDTO(it.id,it.name,it.quantity,it.totalCost,it.category.name))
                return ResponseEntity.ok().body(json)
            }
        }
        return ResponseEntity.internalServerError().body("ERROR")
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{receiptId}/item/{itemId}")
    fun deleteItemFromReceipt(@PathVariable receiptId: Long, @PathVariable itemId: Long)
    {
        itemService.deleteItem(itemId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{receiptId}/item/{itemId}")
    fun updateItem(@PathVariable receiptId: Long,@PathVariable itemId: Long, @RequestBody itemData: ItemDTO)
    {
        itemService.updateItem(Item(itemId,itemData))
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{receiptId}/categorise")
    fun categoriseItems(@PathVariable receiptId: Long)
    {
        categorisingService.categoriseItems(receiptId)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/item/categories")
    fun getCategories():ResponseEntity<String>
    {
        val json: String = gson.toJson(Category.getValidCategoryNames())
        return ResponseEntity.ok().body(json)
    }
}