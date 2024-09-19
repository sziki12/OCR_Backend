package app.ocr_backend.filter

import app.ocr_backend.db_service.DBService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/filter")
@CrossOrigin
class FilterController(private val service: DBService)
{
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun getFilterOptions(): FilterOptionsDto {
        val filterDto = FilterOptionsDto()
        service.getAllReceipt().forEach()
        {
            filterDto.receiptNames.add(it.name)
        }

        service.getPlacesWithReceipts().forEach()
        {
            filterDto.placeNames.add(it.name)
        }
        return filterDto
    }
}