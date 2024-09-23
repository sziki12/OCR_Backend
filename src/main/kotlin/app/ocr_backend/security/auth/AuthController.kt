package app.ocr_backend.security.auth

import app.ocr_backend.household.HouseholdService
import app.ocr_backend.security.dto.CredentialsDto
import app.ocr_backend.security.dto.EmailSaltDto
import app.ocr_backend.security.dto.SignUpDto
import app.ocr_backend.user.UserDTO
import app.ocr_backend.user.UserService
import com.google.gson.Gson
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
class AuthController(
    private val userService: UserService,
    private val userAuthProvider: UserAuthProvider,
    private val tokenService: TokenService,
    private val householdService: HouseholdService,
    ) {

    val gson = Gson()
    @PostMapping("/login")
    fun login(@RequestBody credentials: CredentialsDto):ResponseEntity<String>
    {
        println("CREDENTIALS $credentials")
        val user = userService.loginUser(credentials)
        val userDto = UserDTO(user).also {
            it.token = tokenService.generateToken(user) ?: ""
        }
        val json = gson.toJson(userDto)
        println("LOGIN $userDto")
        return ResponseEntity.ok().body(json)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    fun register(@RequestBody signUpDto: SignUpDto):ResponseEntity<String>
    {
        var user = userService.registerUser(signUpDto)
        val household = householdService.createHouseholdByUser(user,"My Household")
        user = user.also { it.householdUsers.add(household.householdUsers.single()) }
        val userDto = UserDTO(user).also {
            it.token = tokenService.generateToken(user) ?: ""
        }
        val json = gson.toJson(userDto)
        println("REGISTER $userDto")
        return ResponseEntity.ok().body(json)
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/salt")//TODO add email in path
    fun getSalt(@RequestBody usernameDto: EmailSaltDto):ResponseEntity<EmailSaltDto> {

        val user = userService.findByEmail(usernameDto.email)
        return if(user.isPresent)
        {
            val json = EmailSaltDto(usernameDto.email,user.get().salt)
            ResponseEntity.ok().body(json)
        }
        else
            ResponseEntity.notFound().build()
    }
}