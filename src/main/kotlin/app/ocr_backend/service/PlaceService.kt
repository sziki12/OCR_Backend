package app.ocr_backend.service

import app.ocr_backend.model.Place
import app.ocr_backend.repository.PlaceDBRepository
import org.springframework.stereotype.Service

@Service
class PlaceService(val repository:PlaceDBRepository) {

    fun savePlace()
    {
        TODO("savePlace")
    }

    fun deletePlaceById()
    {
        TODO("deletePlaceById")
    }

    fun updatePlace()
    {
        TODO("updatePlace")
    }

    fun getPlaces():List<Place>
    {
        return repository.findAll()
    }
}