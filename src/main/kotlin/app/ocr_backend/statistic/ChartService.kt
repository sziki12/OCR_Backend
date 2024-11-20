package app.ocr_backend.statistic

import app.ocr_backend.household.Household
import app.ocr_backend.receipt.ReceiptService
import enumeration.Category
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class ChartService(private val receiptService: ReceiptService) {
    fun getPieChartData(householdId: UUID, request:ChartRequestDTO): PieChartDTO
    {
        val categories = Category.getValidCategoryNames()
        val receipts = receiptService.getReceiptsByHousehold(householdId)
        val categoryData = ArrayList<ItemCategoryData>()
        val currentDate = LocalDate.now()
        val oneMontBefore = LocalDate.of(currentDate.year,currentDate.month - 1 ,currentDate.dayOfMonth)

        for(category in categories)
        {
            categoryData.add(ItemCategoryData(category,0,0))
        }
        for(receipt in receipts)
        {
            if(request.type == "All Time" ||
                request.type == "Custom" && request.from!! <= receipt.dateOfPurchase && request.to!! >= receipt.dateOfPurchase ||
                request.type == "Last Month" && oneMontBefore <= receipt.dateOfPurchase && currentDate >= receipt.dateOfPurchase)
            {
                for(item in receipt.items)
                {
                    if(item.category != Category.Undefined)
                    {
                        categoryData[item.category.ordinal].itemCount += 1
                        categoryData[item.category.ordinal].totalCost += item.totalCost
                    }
                }
            }
        }
        return PieChartDTO(categoryData)
    }
}