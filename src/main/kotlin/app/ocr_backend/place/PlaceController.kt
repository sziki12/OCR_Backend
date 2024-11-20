package app.ocr_backend.place

import app.ocr_backend.place.dto.PlaceCreateRequest
import app.ocr_backend.place.dto.PlaceResponse
import app.ocr_backend.place.dto.ReceiptResponsePlace
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
    fun getPlaceByReceiptId(@PathVariable receiptId: Long, @PathVariable householdId: UUID): PlaceResponse? {
        val optReceipt = receiptService.getReceipt(householdId, receiptId)
        if (optReceipt.isPresent) {
            return optReceipt.get().place?.toResponse()
        }

        return null
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    fun createPlace(@RequestBody createRequest: PlaceCreateRequest, @PathVariable householdId: UUID): PlaceResponse {
        return placeService.savePlace(householdId, createRequest.toPlace()).get().toResponse()
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/update")
    fun updatePlace(@RequestBody updatedPlace: ReceiptResponsePlace, @PathVariable householdId: UUID): PlaceResponse {
        val receiptPlaces = receiptService.getReceiptsByPlace(householdId,updatedPlace.id)
        return placeService.savePlace(householdId, updatedPlace.toPlace(receiptPlaces)).get().toResponse()
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
    fun getPlaces(@PathVariable householdId: UUID): List<PlaceResponse> {
        return placeService.getPlaces(householdId).map { it.toResponse() }
    }
}