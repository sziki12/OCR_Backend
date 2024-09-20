package app.ocr_backend.household

import app.ocr_backend.household.household_user.HouseholdUser
import app.ocr_backend.item.Item
import app.ocr_backend.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface HouseholdRepository: JpaRepository<Household, UUID> {
}