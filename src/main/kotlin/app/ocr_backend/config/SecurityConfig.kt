package app.ocr_backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {


    @Bean
    fun admin():InMemoryUserDetailsManager
    {
        return InMemoryUserDetailsManager(
            User.withUsername("admin")
                .password("{noop}admin")
                .authorities("read")
                .build()
        )
    }


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain
    {
        return http
                .csrf()
                {
                    it.disable()
                }
                .authorizeHttpRequests()
                {
                    it.anyRequest().authenticated()
                }
                .sessionManagement()
                {
                    it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                }
                .httpBasic(Customizer.withDefaults())
                .build()
    }
}

