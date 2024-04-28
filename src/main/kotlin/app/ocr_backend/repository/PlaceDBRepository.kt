package app.ocr_backend.repository

import app.ocr_backend.model.Place
import org.springframework.data.jpa.repository.JpaRepository

interface PlaceDBRepository:JpaRepository<Place,Long> {

}