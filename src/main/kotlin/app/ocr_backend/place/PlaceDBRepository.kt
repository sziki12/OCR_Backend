package app.ocr_backend.place

import app.ocr_backend.place.Place
import org.springframework.data.jpa.repository.JpaRepository

interface PlaceDBRepository:JpaRepository<Place,Long> {

}