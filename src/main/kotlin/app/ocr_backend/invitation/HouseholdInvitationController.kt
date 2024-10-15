package app.ocr_backend.invitation

import app.ocr_backend.exceptions.ElementNotExists
import app.ocr_backend.household.HouseholdService
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/invitation")
@CrossOrigin
class HouseholdInvitationController(
    private val householdService: HouseholdService,
) {
    @GetMapping("/{invitationId}/accept")
    fun acceptHouseholdInvite(@PathVariable invitationId: UUID): ResponseEntity<Unit> {
        return try {
            householdService.acceptInvitation(invitationId)
            ResponseEntity.accepted().build()
        } catch (e:NoSuchElementException){
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
        catch (e:ElementNotExists){
            ResponseEntity.notFound().build()
        }
    }
}