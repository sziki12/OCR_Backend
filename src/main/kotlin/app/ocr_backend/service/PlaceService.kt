package app.ocr_backend.service

import app.ocr_backend.model.Place
import app.ocr_backend.repository.PlaceDBRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlaceService(val repository:PlaceDBRepository) {

    fun savePlace(place: Place)
    {
        repository.save(place)
    }

    fun deletePlace(placeId:Long)
    {
        repository.deleteById(placeId)
    }

    fun getPlaces():List<Place>
    {
        return repository.findAll()
    }

    fun getPlace(placeId:Long): Optional<Place> {
        return repository.findById(placeId)
    }

    fun validatePlace(placeId:Long)
    {
        val optPlace = repository.findById(placeId)
        if(optPlace.isPresent)
        {
            val place = optPlace.get()
            place.isValidated = true
            repository.save(place)
        }
    }
}