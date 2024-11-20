package app.ocr_backend.household.invitation

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface HouseholdInvitationRepository: JpaRepository<HouseholdInvitation,UUID> {
}