package app.ocr_backend.user.registration_confirmation

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@Entity
data class RegistrationConfirmation(
    val registeredUserId: Long
){
    @Id
    @GeneratedValue
    lateinit var id: UUID

    val timestamp = LocalDateTime.now()
}
