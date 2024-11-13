package app.ocr_backend.user.registration_confirmation

import app.ocr_backend.exceptions.ElementNotExists
import app.ocr_backend.user.UserService
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@CrossOrigin
@RequestMapping("/api/confirmation")
class RegistrationConfirmationController(
    private val userService: UserService,

) {

    @GetMapping("/{confirmationId}")
    fun confirmEmail(@PathVariable confirmationId: UUID): ResponseEntity<Unit>
    {
        try {
            userService.confirmEmail(confirmationId)
        }
        catch (e: EntityNotFoundException){
            return ResponseEntity.notFound().build()
        }
        return  ResponseEntity.ok().build()
    }

    @GetMapping("/request/{email}")
    fun generateEmailConfirmation(@PathVariable email: String): ResponseEntity<Unit>
    {
        try {
            userService.sendEmailConfirmation(email)
        }
        catch (e: ElementNotExists){
            return ResponseEntity.notFound().build()
        }
        return  ResponseEntity.ok().build()
    }
}