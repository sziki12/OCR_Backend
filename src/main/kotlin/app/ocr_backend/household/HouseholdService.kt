package app.ocr_backend.household

import app.ocr_backend.household.household_user.HouseholdUser
import app.ocr_backend.user.User
import org.springframework.stereotype.Service
import java.util.*

@Service
class HouseholdService(
    val householdRepository: HouseholdRepository,
    ) {

    fun getHousehold(householdId: UUID):Optional<Household>{
        return householdRepository.findById(householdId)
    }
    fun isUserInHousehold(user: User, household:Household): Boolean{
        val householdUsers = user.householdUsers
        for (householdUser in householdUsers){
            if(householdUser.household.id == household.id){
                return true
            }
        }
        return false
    }
    fun getHouseholdUser(user: User, household:Household): Optional<HouseholdUser>{
        val householdUsers = user.householdUsers
        for (householdUser in householdUsers){
            if(householdUser.household.id == household.id){
                return Optional.of(householdUser)
            }
        }
        return Optional.empty()
    }
}