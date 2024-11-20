package app.ocr_backend.place

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PlaceRepository:JpaRepository<Place,Long> {
    fun findAllByHouseholdId(householdId: UUID):List<Place>
}