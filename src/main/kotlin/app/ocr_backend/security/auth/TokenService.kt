package app.ocr_backend.security.auth


import app.ocr_backend.user.User
import com.auth0.jwt.exceptions.TokenExpiredException
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class TokenService(
    private val encoder: JwtEncoder,
    private val decoder: JwtDecoder,
) {
    fun generateToken(user: User): String {
        val now = Instant.now()
        val scope = user.householdUsers.map { it.household.id }
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(5,ChronoUnit.MINUTES))
            //.expiresAt(now.plus(5, ChronoUnit.MINUTES))
            .subject(user.email)
            .claim("households", scope)
            .build()
        return encoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }

    fun generateRefreshToken(user:User):String{
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(20,ChronoUnit.MINUTES))
            //.expiresAt(now.plus(6, ChronoUnit.HOURS))
            .subject(user.email)
            .claim("user_id", user.id)
            .build()
        return encoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }

    fun decodeToken(token:String):Jwt{
        val decoded = decoder.decode(token)
        return decoded
    }
    fun decodeAndValidate(token:String):Jwt{
        val decoded = decoder.decode(token)
        if(decoded.expiresAt?.isBefore(Instant.now()) == true){
            throw TokenExpiredException(token,decoded.expiresAt)
        }
        return decoded
    }

    fun validateTokenExpiration(token:String):Boolean{
        try{
            val decodedToken = decoder.decode(token)
            return decodedToken.expiresAt?.isBefore(Instant.now()) == true
        }catch (e:Exception){
            return false
        }
    }
}