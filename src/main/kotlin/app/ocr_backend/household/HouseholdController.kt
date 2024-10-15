package app.ocr_backend.household

import app.ocr_backend.exceptions.ElementNotExists
import app.ocr_backend.security.auth.AuthService
import app.ocr_backend.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/household")
@CrossOrigin
class HouseholdController(
    private val householdService: HouseholdService,
    private val authService: AuthService,
) {
    @GetMapping
    fun getHouseholds(): ResponseEntity<List<Household>> {
        val user = authService.getCurrentUser()
        return if (user.isPresent.not())
            ResponseEntity.notFound().build()
        else
            ResponseEntity.ok(householdService.getHouseholdsByUser(user.get()))
    }

    @PostMapping("{householdId}/invite/{email}")
    fun sendHouseholdInvite(@PathVariable email: String, @PathVariable householdId: UUID): ResponseEntity<Unit> {
        return try {
            householdService.sendInvitationEmail(householdId, email)
            ResponseEntity.ok().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        } catch (e: ElementNotExists) {
            ResponseEntity.notFound().build()
        }
    }
}