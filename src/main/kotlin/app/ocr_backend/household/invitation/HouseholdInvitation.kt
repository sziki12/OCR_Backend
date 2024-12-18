package app.ocr_backend.household.invitation

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class HouseholdInvitation(
    val invitedUserId: Long,
    val senderUserId: Long,
    val householdId: UUID,
){
    @Id
    @GeneratedValue
    lateinit var id: UUID

    val timestamp = LocalDateTime.now()
}
