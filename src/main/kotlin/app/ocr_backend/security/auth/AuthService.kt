package app.ocr_backend.security.auth

import app.ocr_backend.household.HouseholdRepository
import app.ocr_backend.household.HouseholdService
import app.ocr_backend.household.household_user.HouseholdUser
import app.ocr_backend.household.household_user.HouseholdUserService
import app.ocr_backend.user.User
import app.ocr_backend.user.UserRepository
import app.ocr_backend.user.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val userService: UserRepository,
    private val householdUserService: HouseholdUserService
) {
    fun getCurrentUser(): Optional<User> {
        val email = SecurityContextHolder.getContext().authentication.principal.toString()
        println("email: $email")
        return userService.findByEmail(email)
    }

    /***
     * Function can be used to check that is the current user able to access the given household
     */
    fun getCurrentHouseholdUser(householdId: UUID): Optional<HouseholdUser> {
        val currentUser = getCurrentUser()
        println("user: $currentUser")
        if (currentUser.isPresent) {
            return householdUserService.getHouseholdUser(currentUser.get(), householdId)
        }
        return Optional.empty()
    }
}