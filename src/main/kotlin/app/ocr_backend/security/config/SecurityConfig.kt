package app.ocr_backend.security.config

import app.ocr_backend.security.auth.JwtAuthFilter
import app.ocr_backend.security.auth.UserAuthProvider
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val userAuthProvider: UserAuthProvider,
) {

    private val swaggerWhitelist = listOf(
        "/api/swagger-ui/**",
        "/api/swagger-ui.html",
        "/api/api-docs",
        "/api/api-docs.yaml",
        "/api/swagger-resources/**",
        "/api/swagger-resources"
    )
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .addFilterBefore(JwtAuthFilter(userAuthProvider), BasicAuthenticationFilter::class.java)
            .csrf()
            {
                it.disable()
            }
            .sessionManagement()
            {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests()
            {
                it.requestMatchers(HttpMethod.GET,*swaggerWhitelist.toTypedArray()).permitAll()
                it.requestMatchers(HttpMethod.POST, "/login", "/register", "/salt").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer {
                it.jwt { }
            }
            .build()
    }
}

