package app.ocr_backend.config.security

import app.ocr_backend.service.UserService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*


@Component
class UserAuthProvider(val userService:UserService) {

    @Value("${security.jwt.token.secret-key:secret-value}")
    private lateinit var secretKey:String

    //TODO UserService
    fun init()
    {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes())
    }

    fun createToken(login:String): String? {
        val now = Date()
        val validity = Date(now.time + 3_600_000)
        return JWT.create()
            .withIssuer(login)
            .withIssuedAt(now)
            .withExpiresAt(validity)
            .sign(Algorithm.HMAC256(secretKey))
    }

    fun validateToken(token:String): Authentication {
        val verifier = JWT.require(Algorithm.HMAC256(secretKey))
            .build()

        val decoded = verifier.verify(token)

        val user = userService.findByLogin(decoded.issuer)
        return UsernamePasswordAuthenticationToken(user.name,null,Collections.emptyList())
    }

}
