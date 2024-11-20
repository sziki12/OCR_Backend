package app.ocr_backend.household.household_user

import app.ocr_backend.household.Household
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface HouseholdUserRepository:JpaRepository<HouseholdUser, UUID> {
    fun existsByHouseholdIsIn(household: MutableCollection<Household>)
}