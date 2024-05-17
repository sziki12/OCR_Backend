package app.ocr_backend.controller

import app.ocr_backend.config.security.UserAuthProvider
import app.ocr_backend.dto.*
import app.ocr_backend.service.UserService
import com.google.gson.Gson
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
class AuthController(
    val userService: UserService,
    val userAuthProvider: UserAuthProvider,
    ) {

    val gson = Gson()
    @PostMapping("/login")
    fun login(@RequestBody credentials: CredentialsDTO):ResponseEntity<String>
    {
        println("CREDENTIALS $credentials")
        val user = userService.loginUser(credentials)
        val userDto = UserDTO(user).also {
            it.token = userAuthProvider.createToken(it.userName) ?: ""
        }
        val json = gson.toJson(userDto)
        println("LOGIN $userDto")
        return ResponseEntity.ok().body(json)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    fun register(@RequestBody signUpDto: SignUpDTO):ResponseEntity<String>
    {
        val user = userService.registerUser(signUpDto)

        val userDto = UserDTO(user).also {
            it.token = userAuthProvider.createToken(it.userName) ?: ""
        }
        val json = gson.toJson(userDto)
        println("REGISTER $userDto")
        return ResponseEntity.ok().body(json)
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/salt")
    fun getSalt(@RequestBody usernameDto:UserNameSaltDTO):ResponseEntity<String> {

        val user = userService.findByUserName(usernameDto.userName)
        return if(user.isPresent)
        {
            val json = gson.toJson(UserNameSaltDTO(usernameDto.userName,user.get().salt))
            ResponseEntity.ok().body(json)
        }
        else
            ResponseEntity.notFound().build()
    }
}