package app.ocr_backend.user.registration_confirmation

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RegistrationConfirmationRepository: JpaRepository<RegistrationConfirmation, UUID>{
    fun findByRegisteredUserId(registeredUserId: Long):List<RegistrationConfirmation>

    fun deleteAllByRegisteredUserId(registeredUserId: Long)
}