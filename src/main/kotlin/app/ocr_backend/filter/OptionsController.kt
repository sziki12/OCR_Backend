package app.ocr_backend.filter

import app.ocr_backend.place.PlaceService
import app.ocr_backend.receipt.ReceiptService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/household/{householdId}")
@CrossOrigin
class OptionsController(
    private val receiptService: ReceiptService,
    private val placeService: PlaceService,
) {
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/filter")
    fun getFilterOptions(@PathVariable householdId: UUID): FilterOptionsDto {
        val filterDto = FilterOptionsDto()
        receiptService.getReceiptsByHousehold(householdId).forEach()
        {
            if (it.name.isNotEmpty()) {
                filterDto.receiptNames.add(it.name)
            }
        }

        placeService.getPlaces(householdId).forEach()
        {
            if (it.name.isNotEmpty()) {
                filterDto.placeNames.add(it.name)
            }
        }
        return filterDto
    }
}