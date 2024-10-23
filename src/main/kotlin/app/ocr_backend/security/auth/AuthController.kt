package app.ocr_backend.security.auth

import app.ocr_backend.household.HouseholdService
import app.ocr_backend.security.dto.*
import app.ocr_backend.security.refresh_token.RefreshTokenService
import app.ocr_backend.user.UserDTO
import app.ocr_backend.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
class AuthController(
    private val userService: UserService,
    private val userAuthProvider: UserAuthProvider,
    private val tokenService: TokenService,
    private val refreshTokenService:RefreshTokenService,
    private val householdService: HouseholdService,
    private val authService: AuthService,
    ) {

    @PostMapping("/login")
    fun login(@RequestBody credentials: CredentialsDto):ResponseEntity<UserDTO>
    {
        println("CREDENTIALS $credentials")
        val user = userService.loginUser(credentials)
        val refreshToken = tokenService.generateRefreshToken(user)
        val userDto = UserDTO(user).also {
            it.tokens.authToken = tokenService.generateToken(user)
            it.tokens.refreshToken = refreshToken
        }
        refreshTokenService.saveRefreshToken(refreshToken,user)
        println("LOGIN $userDto")
        return ResponseEntity.ok().body(userDto)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    fun register(@RequestBody signUpDto: SignUpDto):ResponseEntity<UserDTO>
    {
        var user = userService.registerUser(signUpDto)
        val household = householdService.createHouseholdByUser(user,"My Household")
        val refreshToken = tokenService.generateRefreshToken(user)
        user = user.also { it.householdUsers.add(household.householdUsers.single()) }
        val userDto = UserDTO(user).also {
            it.tokens.authToken = tokenService.generateToken(user)
            it.tokens.refreshToken = refreshToken
        }
        refreshTokenService.saveRefreshToken(refreshToken,user)
        println("REGISTER $userDto")
        return ResponseEntity.ok().body(userDto)
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/salt")
    fun getSalt(@RequestBody emailDto: EmailSaltDto):ResponseEntity<EmailSaltDto> {

        val user = userService.findByEmail(emailDto.email)
        return if(user.isPresent)
        {
            val dto = EmailSaltDto(emailDto.email,user.get().salt)
            ResponseEntity.ok().body(dto)
        }
        else
            ResponseEntity.notFound().build()
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refresh")
    fun useRefreshToken(@RequestBody refreshTokenDto: RefreshTokenDto): ResponseEntity<TokenDto> {
        try {
            println("CALL useRefreshToken")
            val dto = userAuthProvider.useRefreshToken(refreshTokenDto.refreshToken)
            return ResponseEntity.ok().body(dto)
        }
        catch(e:Exception){
            System.err.println("${e.cause} Token Error: ${e.message}")
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()
        }
    }
}