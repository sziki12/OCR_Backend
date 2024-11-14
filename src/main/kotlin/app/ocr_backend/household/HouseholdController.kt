package app.ocr_backend.household

import app.ocr_backend.exceptions.ElementNotExists
import app.ocr_backend.exceptions.EmailNotSent
import app.ocr_backend.exceptions.NotAdminException
import app.ocr_backend.household.dto.HouseholdUserDto
import app.ocr_backend.household.dto.HouseholdUsersDto
import app.ocr_backend.security.auth.AuthService
import app.ocr_backend.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
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

    @GetMapping("/{householdId}")
    fun getHouseholdUsers(@PathVariable householdId: UUID): ResponseEntity<HouseholdUsersDto> {
        return try {
            val hUser =
                authService.getCurrentHouseholdUser(householdId)
                    .orElseThrow { ElementNotExists.fromHousehold(householdId) }
            val household =
                householdService.getHouseholdById(householdId)
                    .orElseThrow { ElementNotExists.fromHousehold(householdId) }
            val otherUsers = household.householdUsers.filter { it.id != hUser.id }
            val response = HouseholdUsersDto(currentUser = hUser.toDto(), otherUsers = otherUsers.toDto())
            ResponseEntity.ok(response)
        } catch (e: ElementNotExists) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{householdId}/invite/{email}")
    fun sendHouseholdInvite(@PathVariable email: String, @PathVariable householdId: UUID): ResponseEntity<Unit> {
        //TODO Only invite when not self or not already in household
        return try {
            authService.checkUserIsAdmin(householdId)
            householdService.sendInvitationEmail(householdId, email)
            ResponseEntity.ok().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        } catch (e: ElementNotExists) {
            ResponseEntity.notFound().build()
        } catch (e: NotAdminException) {
            ResponseEntity.notFound().build()
        } catch (e: EmailNotSent) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }

    @PostMapping("/{householdName}/new")
    fun createNewHousehold(@PathVariable householdName: String): ResponseEntity<Unit> {
        val user =
            authService.getCurrentUser().orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User Not found") }
        householdService.createHouseholdByUser(user, householdName)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{householdId}/update/{householdName}")
    fun updateHouseholdName(
        @PathVariable householdId: UUID,
        @PathVariable householdName: String
    ): ResponseEntity<Household> {
        return try {
            authService.checkUserIsAdmin(householdId)
            val updatedHousehold = householdService.updateHouseholdName(householdId, householdName)
            ResponseEntity.ok(updatedHousehold)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    /*@DeleteMapping("/{householdId}/delete")
    fun deleteHousehold(@PathVariable householdId: UUID): ResponseEntity<Unit> {
        householdService.deleteHousehold(householdId)
    }*/
}