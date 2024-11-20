package app.ocr_backend.household.household_user

import app.ocr_backend.user.User
import org.springframework.stereotype.Service
import java.util.*

@Service
class HouseholdUserService {
    fun getHouseholdUser(user: User, householdId: UUID): Optional<HouseholdUser> {
        val householdUsers = user.householdUsers
        for (householdUser in householdUsers) {
            if (householdUser.household.id == householdId) {
                return Optional.of(householdUser)
            }
        }
        return Optional.empty()
    }
}