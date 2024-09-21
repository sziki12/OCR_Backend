package app.ocr_backend.household

import app.ocr_backend.household.household_user.HouseholdUser
import app.ocr_backend.household.household_user.HouseholdUserRepository
import app.ocr_backend.user.User
import app.ocr_backend.user.UserService
import org.springframework.stereotype.Service
import java.util.*

@Service
class HouseholdService(
    val householdRepository: HouseholdRepository,
    val householdUserRepository: HouseholdUserRepository,
    val userService: UserService,
) {

    /*fun getHousehold(householdId: UUID): Optional<Household> {
        return householdRepository.findById(householdId)
    }*/

    fun isUserInHousehold(user: User, household: Household): Boolean {
        val householdUsers = user.householdUsers
        for (householdUser in householdUsers) {
            if (householdUser.household.id == household.id) {
                return true
            }
        }
        return false
    }

    fun getHouseholdUser(user: User, householdId: UUID): Optional<HouseholdUser> {
        val householdUsers = user.householdUsers
        for (householdUser in householdUsers) {
            if (householdUser.household.id == householdId) {
                return Optional.of(householdUser)
            }
        }
        return Optional.empty()
    }

    fun createHouseholdByUser(user: User, name: String): Household {
        var household = Household(name)
        val householdUser = HouseholdUser(true).also {
            it.household = household
            it.user = user
        }
        household = household.also { it.householdUsers.add(householdUser) }
        householdRepository.save(household)
        householdUserRepository.save(householdUser)
        return household
    }
}