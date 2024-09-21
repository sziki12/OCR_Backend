package app.ocr_backend.security.auth

import app.ocr_backend.household.HouseholdService
import app.ocr_backend.security.dto.CredentialsDTO
import app.ocr_backend.security.dto.EmailSaltDTO
import app.ocr_backend.security.dto.SignUpDTO
import app.ocr_backend.user.UserService
import app.ocr_backend.user.UserDTO
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
    fun login(@RequestBody credentials: CredentialsDTO):ResponseEntity<String>
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
    fun register(@RequestBody signUpDto: SignUpDTO):ResponseEntity<String>
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
    @PostMapping("/salt")
    fun getSalt(@RequestBody usernameDto: EmailSaltDTO):ResponseEntity<String> {

        val user = userService.findByEmail(usernameDto.email)
        return if(user.isPresent)
        {
            val json = gson.toJson(EmailSaltDTO(usernameDto.email,user.get().salt))
            ResponseEntity.ok().body(json)
        }
        else
            ResponseEntity.notFound().build()
    }
}