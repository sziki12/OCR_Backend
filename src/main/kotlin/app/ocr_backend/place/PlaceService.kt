package app.ocr_backend.place

import app.ocr_backend.household.Household
import app.ocr_backend.receipt.ReceiptService
import app.ocr_backend.security.auth.AuthService
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlaceService(
    val repository: PlaceRepository,
    val receiptService: ReceiptService,
    val authService: AuthService
) {

    fun savePlace(place: Place): Place {
        return repository.save(place)
    }

    fun deletePlace(placeId: Long) {
        repository.deleteById(placeId)
    }

    fun getPlaces(): List<Place> {
        return repository.findAll()
    }

    fun getPlace(placeId: Long): Optional<Place> {
        return repository.findById(placeId)
    }

    fun validatePlace(placeId: Long) {
        val optPlace = repository.findById(placeId)
        if (optPlace.isPresent) {
            val place = optPlace.get()
            place.isValidated = true
            repository.save(place)
        }
    }

    fun mergePlaces(holderId: Long, partId: Long) {
        val holder = repository.findById(holderId)
        val part = repository.findById(partId)
        if (holder.isPresent && part.isPresent) {
            for (receipt in part.get().receipts) {
                receipt.place = holder.get()
                receiptService.saveReceipt(receipt)
            }
            repository.deleteById(partId)
        }
    }

    fun getPlacesWithReceipts(householdId: UUID): List<Place> {//TODO householdUser unused
        val householdUser = authService.getCurrentHouseholdUser(householdId)
        val receipts = receiptService.getReceiptsByHousehold(householdId)
        val places = HashSet<Place>()
        receipts.forEach()
        {
            val place = it.place
            if (place != null && !places.contains(place)) {
                places.add(place)
            }
        }
        return places.toList()
    }


}