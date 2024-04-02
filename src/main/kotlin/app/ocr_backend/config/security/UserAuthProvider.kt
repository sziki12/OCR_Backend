package app.ocr_backend.config.security

import app.ocr_backend.model.User
import app.ocr_backend.service.UserService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*


@Component
class UserAuthProvider(val userService:UserService) {

    //TODO REMOVE KEY FROM CODE
    private var secretKey:String = "secret_key"

    init
    {
        secretKey = Base64.getEncoder().encodeToString(secretKey.encodeToByteArray())
    }

    fun createToken(userName:String): String? {
        val now = Date()
        val validity = Date(now.time + 3_600_000)
        return JWT.create()
            .withIssuer(userName)
            .withIssuedAt(now)
            .withExpiresAt(validity)
            .sign(Algorithm.HMAC256(secretKey))
    }

    fun validateToken(token:String): Authentication? {
        val user = getUserByToken(token)
        if(user.isPresent)
        {
            return UsernamePasswordAuthenticationToken(user.get().userName,null,Collections.emptyList())
        }
        return null
    }

    private fun getUserByToken(token: String): Optional<User> {
        val verifier = JWT.require(Algorithm.HMAC256(secretKey))
            .build()

        val decoded = verifier.verify(token)

        return userService.findByUserName(decoded.issuer)
    }

}
