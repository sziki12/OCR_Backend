package app.ocr_backend.filter

import app.ocr_backend.db_service.DBService
import app.ocr_backend.household.HouseholdService
import app.ocr_backend.receipt.ReceiptService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api")
@CrossOrigin
class FilterController(
    private val receiptService: ReceiptService,
    private val householdService: HouseholdService)
{
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/household/{householdId}/filter")
    fun getFilterOptions(@PathVariable householdId: UUID): FilterOptionsDto {
        val filterDto = FilterOptionsDto()
        val household  = householdService.getHousehold(householdId)
        if(household.isPresent){
            receiptService.getReceiptsByHousehold(household.get()).forEach()
            {
                filterDto.receiptNames.add(it.name)
            }

            receiptService.getReceiptsByHousehold(household.get()).forEach()
            {
                filterDto.placeNames.add(it.name)
            }
        }
        return filterDto
    }
}