package app.ocr_backend.security.refresh_token

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RefreshTokenRepository: JpaRepository<RefreshToken,Long>{
    fun findByToken(token: String): Optional<RefreshToken>
    fun deleteByToken(token: String)
}