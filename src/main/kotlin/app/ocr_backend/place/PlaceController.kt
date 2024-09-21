package app.ocr_backend.place

import app.ocr_backend.receipt.ReceiptService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/household/{householdId}/place")
@CrossOrigin
class PlaceController(
    val receiptService: ReceiptService,
    val placeService: PlaceService
) {
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/{receiptId}")
    fun getPlaceByReceiptId(@PathVariable receiptId: Long, @PathVariable householdId: UUID): Place? {
        val optReceipt = receiptService.getReceipt(householdId, receiptId)
        if (optReceipt.isPresent) {
            return optReceipt.get().place
        }

        return null
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/save")
    fun savePlace(@RequestBody place: Place, @PathVariable householdId: UUID): Place {
        return placeService.savePlace(place)//TODO householdId
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{placeId}/to/{receiptId}")
    fun assignPlace(@PathVariable receiptId: Long, @PathVariable placeId: Long, @PathVariable householdId: UUID) {
        receiptService.assignPlaceToReceipt(householdId, receiptId, placeId)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/remove/{receiptId}")
    fun removePlace(@PathVariable receiptId: Long, @PathVariable householdId: UUID) {
        receiptService.removePlaceFromReceipt(householdId, receiptId)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/merge/{partId}/into/{holderId}")
    fun margePlaces(@PathVariable holderId: Long, @PathVariable partId: Long, @PathVariable householdId: UUID) {
        placeService.mergePlaces(householdId, holderId, partId)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/validate/{placeId}")
    fun validatePlace(@PathVariable placeId: Long, @PathVariable householdId: UUID) {
        placeService.validatePlace(placeId)//TODO householdId
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun getPlaces(@PathVariable householdId: UUID): List<Place> {
        return placeService.getPlaces()//TODO householdId
    }
}