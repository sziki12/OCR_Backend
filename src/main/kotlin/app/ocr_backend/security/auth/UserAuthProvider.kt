package app.ocr_backend.security.auth

import app.ocr_backend.user.User
import app.ocr_backend.user.UserDTO
import app.ocr_backend.user.UserService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Component
import java.util.*


@Component
class UserAuthProvider(val userService: UserService,
    val decoder: JwtDecoder) {

    @Value("\${security.jwt.token.secret-key:another-key}")
    private var secretKey:String = ""

    init
    {
        secretKey = Base64.getEncoder().encodeToString(secretKey.encodeToByteArray())
    }

    fun validateToken(token:String): Authentication? {
        val user = getUserByToken(token)
        if(user.isPresent)
        {
            return UsernamePasswordAuthenticationToken(user.get().email,null,Collections.emptyList())
        }
        return null
    }

    fun getUserByToken(token: String): Optional<User> {
        //val verifier = JWT.require(Algorithm.HMAC256(secretKey))
        //    .build()

        val decoded = decoder.decode(token)//verifier.verify(token)

        return userService.findByEmail(decoded.subject)
    }

}
