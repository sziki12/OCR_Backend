package app.ocr_backend.security.auth

import app.ocr_backend.exceptions.InvalidTokenException
import app.ocr_backend.exceptions.UsedTokenException
import app.ocr_backend.security.dto.TokenDto
import app.ocr_backend.security.refresh_token.RefreshToken
import app.ocr_backend.security.refresh_token.RefreshTokenRepository
import app.ocr_backend.user.User
import app.ocr_backend.user.UserService
import com.auth0.jwt.exceptions.TokenExpiredException
import jakarta.transaction.Transactional
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*


@Component
class UserAuthProvider(
    private val userService: UserService,
    private val tokenService: TokenService,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun validateToken(token: String): Authentication? {
        val user = getUserByToken(token)
        if(tokenService.validateTokenExpiration(token))
            throw TokenExpiredException(token, Instant.now())

        if (user.isPresent) {
            return UsernamePasswordAuthenticationToken(user.get().email, null, Collections.emptyList())
        }
        return null
    }

    @Transactional
    fun useRefreshToken(token: String): TokenDto {
        val decodedToken = tokenService.decodeAndValidate(token)

        val storedRefreshToken = refreshTokenRepository.findByToken(token).orElseThrow{InvalidTokenException(token)}
        val user = userService.findByEmail(decodedToken.subject).get()
        if(!user.refreshTokens.contains(storedRefreshToken))
            throw UsedTokenException(token)
        //Delete old refresh token
        refreshTokenRepository.deleteByToken(token)
        //Generate new token pair
        val authToken = tokenService.generateToken(user)
        val refreshToken = tokenService.generateRefreshToken(user)
        refreshTokenRepository.save(RefreshToken(refreshToken).also { it.user=user })

        return TokenDto(authToken,refreshToken)
    }

    fun getUserByToken(token: String): Optional<User> {
        val decoded = tokenService.decodeToken(token)
        return userService.findByEmail(decoded.subject)
    }

}
