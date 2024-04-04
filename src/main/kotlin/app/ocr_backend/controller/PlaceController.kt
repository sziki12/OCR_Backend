package app.ocr_backend.controller

import app.ocr_backend.model.Item
import app.ocr_backend.model.Place
import app.ocr_backend.service.DBService
import app.ocr_backend.service.PlaceService
import com.google.gson.Gson
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/place")
@CrossOrigin
class PlaceController(val service:DBService) {
    val gson = Gson()
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/save")
    fun savePlace(@RequestBody place: Place): Optional<Place> {
        return service.savePlace(place)
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun getPlaces(): List<Place> {
        return service.getPlaces()
    }
}