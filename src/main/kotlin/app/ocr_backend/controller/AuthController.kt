package app.ocr_backend.controller

import app.ocr_backend.config.security.UserAuthProvider
import app.ocr_backend.dto.CredentialsDTO
import app.ocr_backend.dto.SignUpDTO
import app.ocr_backend.dto.UserDTO
import app.ocr_backend.service.UserService
import com.google.gson.Gson
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    val userService: UserService,
    val userAuthProvider: UserAuthProvider,
    ) {

    val gson = Gson()
    @PostMapping("/login")
    fun login(@RequestBody credentials: CredentialsDTO):ResponseEntity<String>
    {
        val user = userService.loginUser(credentials)
        val userDto = UserDTO(user).let {
            it.token = userAuthProvider.createToken(it.login) ?: ""
        }
        val json = gson.toJson(userDto)
        return ResponseEntity.ok().body(json)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    fun register(@RequestBody signUpDto: SignUpDTO):ResponseEntity<String>
    {
        val user = userService.registerUser(signUpDto)

        val userDto = UserDTO(user).let {
            it.token = userAuthProvider.createToken(it.login) ?: ""
        }
        val json = gson.toJson(userDto)
        return ResponseEntity.ok().body(json)
    }
}