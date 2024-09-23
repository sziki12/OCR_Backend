package app.ocr_backend.filter

import app.ocr_backend.receipt.ReceiptService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/household/{householdId}")
@CrossOrigin
class FilterController(
    private val receiptService: ReceiptService,
) {
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/filter")
    fun getFilterOptions(@PathVariable householdId: UUID): FilterOptionsDto {
        val filterDto = FilterOptionsDto()
        receiptService.getReceiptsByHousehold(householdId).forEach()
        {
            filterDto.receiptNames.add(it.name)
        }

        receiptService.getReceiptsByHousehold(householdId).forEach()
        {
            filterDto.placeNames.add(it.name)
        }
        return filterDto
    }
}