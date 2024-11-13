package app.ocr_backend.household

import app.ocr_backend.email.EmailService
import app.ocr_backend.exceptions.ElementNotExists
import app.ocr_backend.household.household_user.HouseholdUser
import app.ocr_backend.household.household_user.HouseholdUserRepository
import app.ocr_backend.household.invitation.HouseholdInvitationService
import app.ocr_backend.security.auth.AuthService
import app.ocr_backend.user.User
import app.ocr_backend.user.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class HouseholdService(
    val householdRepository: HouseholdRepository,
    val householdUserRepository: HouseholdUserRepository,
    val userService: UserService,
    val authService: AuthService,
    val householdInvitationService: HouseholdInvitationService,
    val emailService: EmailService,
) {

    @Value("\${server.url}")
    lateinit var serverUrl:String

    fun isUserInHousehold(user: User, household: Household): Boolean {
        val householdUsers = user.householdUsers
        for (householdUser in householdUsers) {
            if (householdUser.household.id == household.id) {
                return true
            }
        }
        return false
    }

    fun createHouseholdByUser(user: User, name: String): Household {
        val household = Household(name)
        var householdUser = HouseholdUser(true).also {
            it.household = household
            it.user = user
        }
        householdUser = addHouseholdUserToHousehold(householdUser,household)
        return householdUser.household
    }

    fun updateHouseholdName(householdId: UUID,name: String): Household {
        val hUser = authService.getCurrentHouseholdUser(householdId).orElseThrow{ElementNotExists.fromHousehold(householdId)}
        val household = hUser.household
        return householdRepository.save(household.also { it.name=name })
    }

    fun getHouseholdsByUser(user: User):List<Household>{
        return user.householdUsers.map { it.household }
    }

    fun getHouseholdById(householdId: UUID): Optional<Household> {
        return householdRepository.findById(householdId)
    }

    fun sendInvitationEmail(householdId: UUID, targetEmail:String){
        val household = this.getHouseholdById(householdId).get()
        val sender = authService.getCurrentUser().get()
        val invitedUser = userService.findByEmail(targetEmail).orElseThrow{ElementNotExists.fromUser(targetEmail)}
        val invitation = householdInvitationService.createInvitation(sender,invitedUser,household)

        val joinUrl = "$serverUrl/api/invitation/${invitation.id}/accept"
        val subject = "Household Invitation"
        val content = "You have been invited to join a household by user: ${sender.name} email: ${sender.email} Please click on this link if you would like to join the ${household.name} household $joinUrl"
        emailService.sendEmail(targetEmail,subject,content)
    }

    fun acceptInvitation(invitationId: UUID){
        val invitation = householdInvitationService.findById(invitationId).orElseThrow{ElementNotExists.fromHouseholdInvitation(invitationId)}
        val invitedUser = userService.findById(invitation.invitedUserId).get()
        val household = householdRepository.findById(invitation.householdId).get()

        val newHouseholdUser = HouseholdUser(true).also {
            it.household = household
            it.user = invitedUser
        }
        addHouseholdUserToHousehold(newHouseholdUser,household)
        householdInvitationService.deleteById(invitationId)
    }

    private fun addHouseholdUserToHousehold(householdUser: HouseholdUser, household:Household): HouseholdUser {
        householdRepository.save(household.also{ it.householdUsers.add(householdUser)})
        return householdUserRepository.save(householdUser)
    }
}