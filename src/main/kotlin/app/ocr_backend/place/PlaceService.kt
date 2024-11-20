package app.ocr_backend.place

import app.ocr_backend.receipt.Receipt
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

    fun savePlace(householdId: UUID,place: Place): Optional<Place> {
        val optHUser = authService.getCurrentHouseholdUser(householdId)
        if (optHUser.isPresent.not())
            return Optional.empty<Place>()
        return Optional.of(repository.save(place.also { it.household = optHUser.get().household}))
    }

    fun deletePlace(householdId: UUID,placeId: Long) {//TODO householdUser
        repository.deleteById(placeId)
    }

    fun getPlaces(householdId: UUID): List<Place> {
        val optHUser = authService.getCurrentHouseholdUser(householdId)
       return if (optHUser.isPresent.not())
           listOf()
        else
            repository.findAllByHouseholdId(householdId)
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

    fun mergePlaces(householdId: UUID, holderId: Long, partId: Long) {
        val holder = repository.findById(holderId)
        val part = repository.findById(partId)
        if (holder.isPresent && part.isPresent) {
            for (receipt in part.get().receipts) {
                receipt.place = holder.get()
                receiptService.saveReceipt(householdId, receipt)
            }
            repository.deleteById(partId)
        }
    }

    /*fun getPlacesWithReceipts(householdId: UUID): List<Place> {//TODO householdUser unused
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
    }*/


}