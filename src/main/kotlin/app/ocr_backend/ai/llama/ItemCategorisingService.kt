package app.ocr_backend.ai.llama

import app.ocr_backend.receipt.Receipt
import app.ocr_backend.db_service.DBService
import app.ocr_backend.household.Household
import app.ocr_backend.item.ItemService
import app.ocr_backend.receipt.ReceiptService
import com.google.gson.Gson
import enumeration.Category
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class ItemCategorisingService(
    //val dbService: DBService,
    val receiptService: ReceiptService,
    val itemService: ItemService,
    val llamaService: LlamaService
) {
    private val maxRetryCount: Int = 10
    fun categoriseItems(
        householdId: UUID,
        receiptId: Long,
        itemsToCategorise: MutableList<String>? = null,
        numberOfRuns: Int = 1
    ) {
        val optReceipt = receiptService.getReceipt(householdId, receiptId)
        if (!optReceipt.isPresent)
            return //TODO Throw error
        val receipt = optReceipt.get()
        val itemIds = HashMap<String, Long>()
        val itemNames = itemsToCategorise ?: ArrayList()
        val hasStartingItems = itemNames.isNotEmpty()
        for (item in receipt.items) {
            if (!hasStartingItems) {
                itemNames.add(item.name)
            }
            itemIds[item.name] = item.id
        }
        //println("LLAMA CATEGORISATION START")
        val json = llamaService.categoriseItems(receipt.name, itemNames, Category.getValidCategoryNames())
        val gson = Gson()

        try {
            val response = gson.fromJson(json, CategoriesDTO::class.java)
            for (category in Category.getValidCategories()) {
                when (category) {
                    Category.Clothing -> response.Clothing?.let { setEveryCategoryInReceipt(itemIds, it, category) }
                    Category.Food -> response.Food?.let { setEveryCategoryInReceipt(itemIds, it, category) }
                    Category.Entertainment -> response.Entertainment?.let {
                        setEveryCategoryInReceipt(
                            itemIds,
                            it,
                            category
                        )
                    }

                    Category.Household -> response.Household?.let { setEveryCategoryInReceipt(itemIds, it, category) }
                    Category.Housing -> response.Housing?.let { setEveryCategoryInReceipt(itemIds, it, category) }
                    Category.Personal -> response.Personal?.let { setEveryCategoryInReceipt(itemIds, it, category) }
                    Category.Utilities -> response.Utilities?.let { setEveryCategoryInReceipt(itemIds, it, category) }
                    Category.Other -> response.Other?.let { setEveryCategoryInReceipt(itemIds, it, category) }
                    Category.Undefined -> {}
                }
            }
        } catch (e: Exception) {
            println("Failed to Process Categorisation response")
        }
        val uncategorizedItems = getUncategorizedItems(itemIds, itemNames)
        if (uncategorizedItems.isNotEmpty() && numberOfRuns < maxRetryCount) {
            println("Restarting Categorisation Process")
            println("Uncategorized Items: $uncategorizedItems")
            categoriseItems(householdId, receipt.id, uncategorizedItems, numberOfRuns + 1)
        }
    }


    private fun getUncategorizedItems(itemIds: HashMap<String, Long>, itemNames: List<String>): MutableList<String> {
        val list = ArrayList<String>()
        for (name in itemNames) {
            val optItem = itemIds[name]?.let { itemService.getItem(it) }
            if (optItem?.isPresent == true) {
                val item = optItem.get()
                if (item.category == Category.Undefined) {
                    list.add(item.name)
                }
            }
        }
        return list
    }

    private fun setEveryCategoryInReceipt(itemId: HashMap<String, Long>, itemNames: List<String>, category: Category) {
        for (name in itemNames) {
            itemId[name]?.let { setItemCategory(it, category) }
        }
    }

    private fun setItemCategory(itemId: Long, category: Category) {
        val optItem = itemService.getItem(itemId)
        if (optItem.isPresent) {
            val item = optItem.get()
            item.category = category
            itemService.updateItem(item)
        }
    }
}