package app.ocr_backend.invitation

import app.ocr_backend.household.Household
import app.ocr_backend.user.User
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class HouseholdInvitationService(
    private val householdInvitationRepository: HouseholdInvitationRepository
) {
    fun createInvitation(sender: User,invitedUser:User,household: Household): HouseholdInvitation {
        val invitation = HouseholdInvitation(
            householdId = household.id,
            invitedUserId = invitedUser.id,
            senderUserId = sender.id
        )
        return householdInvitationRepository.save(invitation)
    }

    fun deleteById(id: UUID){
        householdInvitationRepository.deleteById(id)
    }
    fun findById(id: UUID): Optional<HouseholdInvitation> {
        return householdInvitationRepository.findById(id)
    }

    @Scheduled(cron = "0 0 * * * *")
    fun deleteOldInvitations(){
        val now = LocalDateTime.now()
        val invitations = householdInvitationRepository.findAll()
        val toDelete = mutableListOf<HouseholdInvitation>()
        for(invitation in invitations){
            val expiration = invitation.timestamp.plusDays(1)
            if(now.isAfter(expiration)){
                toDelete.add(invitation)
            }
        }
        householdInvitationRepository.deleteAll(toDelete)
    }
}