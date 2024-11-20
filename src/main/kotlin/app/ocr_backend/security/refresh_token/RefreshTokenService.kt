package app.ocr_backend.security.refresh_token

import app.ocr_backend.security.auth.TokenService
import app.ocr_backend.user.User
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenService: TokenService
) {
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    fun deleteExpiredRefreshTokens(){
        println("DeleteExpiredRefreshTokens")
        val tokens = refreshTokenRepository.findAll()
        val tokensToDelete = mutableListOf<RefreshToken>()
        for(refreshToken in tokens){
            if(!tokenService.validateTokenExpiration(refreshToken.token)){
                tokensToDelete.add(refreshToken)
            }
        }
        refreshTokenRepository.deleteAll(tokensToDelete)
    }

    @Transactional
    fun saveRefreshToken(token:String, user: User){
        refreshTokenRepository.save(RefreshToken(token).also { it.user=user })
    }
}