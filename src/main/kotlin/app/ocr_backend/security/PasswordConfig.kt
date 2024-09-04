package app.ocr_backend.security

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordConfig {

    @Bean
    fun passwordEncoder():PasswordEncoder
    {
        return BCryptPasswordEncoder()
    }
}