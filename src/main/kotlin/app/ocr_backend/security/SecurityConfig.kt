package app.ocr_backend.security

import app.ocr_backend.security.auth.JwtAuthFilter
import app.ocr_backend.security.auth.UserAuthProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(val userAuthProvider: UserAuthProvider) {


    /*@Bean
    fun admin():InMemoryUserDetailsManager
    {
        return InMemoryUserDetailsManager(
            User.withUsername("admin")
                .password("{noop}admin")
                .authorities("read")
                .build()
        )
    }*/


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain
    {
        return http
            .addFilterBefore(JwtAuthFilter(userAuthProvider),BasicAuthenticationFilter::class.java)
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
                it.requestMatchers(HttpMethod.POST,"/login","/register","/salt").permitAll()
                it.anyRequest().authenticated()
            }
            .build()
    }
}

