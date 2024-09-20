package app.ocr_backend.place

import org.springframework.data.jpa.repository.JpaRepository

interface PlaceRepository:JpaRepository<Place,Long> {

}