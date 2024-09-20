package app.ocr_backend.place

import app.ocr_backend.receipt.ReceiptService
import com.google.gson.Gson
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/place")
@CrossOrigin
class PlaceController(
    val receiptService: ReceiptService,
    val placeService: placeService
) {
    val gson = Gson()

    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/{receiptId}")
    fun getPlaceByReceiptId(@PathVariable receiptId: Long): Place? {
        val optReceipt = receiptService.getReceipt(receiptId)
        if(optReceipt.isPresent)
        {
            return optReceipt.get().place
        }

        return null;
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/save")
    fun savePlace(@RequestBody place: Place): Place {
        return placeService.savePlace(place)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{placeId}/to/{receiptId}")
    fun assignPlace(@PathVariable receiptId:Long,@PathVariable placeId: Long)
    {
        receiptService.assignPlaceToReceipt(receiptId,placeId)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/remove/{receiptId}")
    fun removePlace(@PathVariable receiptId:Long)
    {
        receiptService.removePlaceFromReceipt(receiptId)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/merge/{partId}/into/{holderId}")
    fun margePlaces(@PathVariable holderId:Long,@PathVariable partId: Long)
    {
        placeService.mergePlaces(holderId,partId)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/validate/{placeId}")
    fun validatePlace(@PathVariable placeId: Long)
    {
        placeService.validatePlace(placeId)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    fun getPlaces(): List<Place> {
        return placeService.getPlaces()
    }
}