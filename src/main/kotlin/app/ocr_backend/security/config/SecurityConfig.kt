package app.ocr_backend.security.config

import app.ocr_backend.security.auth.JwtAuthFilter
import app.ocr_backend.security.auth.UserAuthProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableScheduling
class SecurityConfig(
    val userAuthProvider: UserAuthProvider,
) {

    private val swaggerWhitelist = listOf(
        "/api/swagger-ui/**",
        "/api/swagger-ui.html",
        "/api/api-docs",
        "/api/api-docs.yaml",
        "/api/swagger-resources/**",
        "/api/swagger-resources",
    )

    private val pathWhitelistPost = listOf(
        "/login",
        "/register",
        "/salt",
        "/refresh"
    )
    private val pathWhiteListGet = listOf(
        "/api/invitation/*/accept",
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
                it.requestMatchers(HttpMethod.GET, *swaggerWhitelist.toTypedArray()).permitAll()
                it.requestMatchers(HttpMethod.GET, *pathWhiteListGet.toTypedArray()).permitAll()
                it.requestMatchers(HttpMethod.POST, *pathWhitelistPost.toTypedArray()).permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer {
                it.jwt { }
            }
            .build()
    }
}

