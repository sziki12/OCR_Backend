package app.ocr_backend.ai.categorisation

import app.ocr_backend.ai.categorisation.backend_dto.CategoriseParams
import app.ocr_backend.item.ItemService
import app.ocr_backend.receipt.ReceiptService
import com.google.gson.Gson
import enumeration.Category
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class ItemCategorisingService(
    val receiptService: ReceiptService,
    val itemService: ItemService,
) {
    private val gson = Gson()
    private val url = "http://localhost:9090/categorise"
    fun categoriseItems(
        householdId: UUID,
        receiptId: Long,
        categorise_model: String
    ) {
        val optReceipt = receiptService.getReceipt(householdId, receiptId)
        if (!optReceipt.isPresent)
            return //TODO Throw error
        val receipt = optReceipt.get()
        val itemIds = HashMap<String, Long>()
        val itemNames = mutableListOf<String>()
        for (item in receipt.items) {
            if (item.category == Category.Undefined) {
                val currentItemName = item.name.lowercase()
                itemNames.add(currentItemName)
                itemIds[currentItemName] = item.id
            }
        }

        try {

            val response = sendCategorizeRequest(CategoriseParams(
                categorise_model = categorise_model,
                items = stringListToColumnSeparatedString(itemNames),
                categories = stringListToColumnSeparatedString(Category.getValidCategories().map { it.name })
            ))
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
        /*val uncategorizedItems = getUncategorizedItems(itemIds, itemNames)
        if (uncategorizedItems.isNotEmpty() && numberOfRuns < maxRetryCount) {
            println("Restarting Categorisation Process")
            println("Uncategorized Items: $uncategorizedItems")
            categoriseItems(householdId, receipt.id, uncategorizedItems, numberOfRuns + 1)
        }*/
    }


    /*private fun getUncategorizedItems(itemIds: HashMap<String, Long>, itemNames: List<String>): MutableList<String> {
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
    }*/

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

    private fun sendCategorizeRequest(categoriseParams: CategoriseParams): CategoriesDTO {
        val jsoType = "application/json".toMediaType()

        val client = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build()
        val json = gson.toJson(
            categoriseParams
        )
        val body = json.toRequestBody(jsoType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        //TODO Handle IOExcepton
        val response = client.newCall(request).execute()
        println("PYTHON RESPONSE")
        val responseBody = response.body?.string()
        println(responseBody)
        return gson.fromJson(responseBody, CategoriesDTO::class.java)
    }

    private fun stringListToColumnSeparatedString(input: List<String>): String{
        val stringBuilder = StringBuilder()
        input.forEach{stringBuilder.append("$it,")}
        stringBuilder.substring(0,stringBuilder.length-1)
        return stringBuilder.toString()
    }
}