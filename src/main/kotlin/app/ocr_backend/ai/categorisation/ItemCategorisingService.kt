package app.ocr_backend.ai.categorisation

import app.ocr_backend.ai.categorisation.backend_dto.CategoriseParams
import app.ocr_backend.exceptions.ElementNotExists
import app.ocr_backend.item.ItemService
import app.ocr_backend.receipt.ReceiptService
import com.google.gson.Gson
import enumeration.Category
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class ItemCategorisingService(
    val receiptService: ReceiptService,
    val itemService: ItemService,
) {
    private val gson = Gson()
    @Value("\${python.url}")
    lateinit var pythonUrl: String
    fun categoriseItems(
        householdId: UUID,
        receiptId: Long,
        categoriseModel: String
    ) {
        val receipt = receiptService.getReceipt(householdId, receiptId)
            .orElseThrow { ElementNotExists.fromReceipt(householdId, receiptId) }
        val itemIds = HashMap<String, MutableList<Long>>()
        val itemNames = mutableListOf<String>()
        for (item in receipt.items) {
            if (item.category == Category.Undefined) {
                val currentItemName = item.name.lowercase()
                itemNames.add(currentItemName)
                if (itemIds[currentItemName] != null) {
                    itemIds[currentItemName]?.add(item.id)
                } else {
                    itemIds[currentItemName] = mutableListOf(item.id)
                }
            }
        }

        try {

            val response = sendCategorizeRequest(
                CategoriseParams(
                    categorise_model = categoriseModel,
                    items = stringListToColumnSeparatedString(itemNames),
                    categories = stringListToColumnSeparatedString(Category.getValidCategories().map { it.name })
                )
            )
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
    }

    private fun setEveryCategoryInReceipt(
        itemIdMap: HashMap<String, MutableList<Long>>,
        itemNames: List<String>,
        category: Category
    ) {
        for (name in itemNames) {
            itemIdMap[name]?.let { itemIds ->
                for (itemId in itemIds) {
                    setItemCategory(itemId, category)
                }
            }
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
        val url = "$pythonUrl/categorise"
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

    private fun stringListToColumnSeparatedString(input: List<String>): String {
        val stringBuilder = StringBuilder()
        input.forEach { stringBuilder.append("$it,") }
        stringBuilder.substring(0, stringBuilder.length - 1)
        return stringBuilder.toString()
    }
}