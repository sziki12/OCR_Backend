package app.ocr_backend.household

import app.ocr_backend.security.auth.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/household")
@CrossOrigin
class HouseholdController(
    private val householdService: HouseholdService,
    private val authService: AuthService,) {
    @GetMapping
    fun getHouseholds(): ResponseEntity<List<Household>>{
        val user = authService.getCurrentUser()
        if(user.isPresent.not())
            ResponseEntity.notFound()
        return ResponseEntity.ok(householdService.getHouseholdsByUser(user.get()))
    }
}