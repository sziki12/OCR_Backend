package app.ocr_backend.security.auth

import app.ocr_backend.household.Household
import app.ocr_backend.household.HouseholdService
import app.ocr_backend.household.household_user.HouseholdUser
import app.ocr_backend.user.User
import app.ocr_backend.user.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val userService: UserService,
    private val householdService: HouseholdService
) {
    fun getCurrentUser(): Optional<User> {
        val email = SecurityContextHolder.getContext().authentication.principal.toString()
        return userService.findByEmail(email)
    }

    fun getCurrentHouseholdUser(household: Household): Optional<HouseholdUser> {
        val currentUser = getCurrentUser()
        if (currentUser.isPresent) {
            return householdService.getHouseholdUser(currentUser.get(), household)
        }
        return Optional.empty()
    }
}