package app.ocr_backend.controller

import app.ocr_backend.model.Item
import app.ocr_backend.model.Place
import app.ocr_backend.service.DBService
import app.ocr_backend.service.PlaceService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/place")
@CrossOrigin
class PlaceController(val service:DBService) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/save")
    fun savePlace(@RequestBody place: Place)
    {
        service.savePlace(place)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{placeId}/to/{receiptId}")
    fun assignPlace(@PathVariable receiptId:Long,@PathVariable placeId: Long)
    {
        service.assignPlaceToReceipt(receiptId,placeId)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/validate/{placeId}")
    fun validatePlace(@PathVariable placeId: Long)
    {
        service.validatePlace(placeId)
    }
}