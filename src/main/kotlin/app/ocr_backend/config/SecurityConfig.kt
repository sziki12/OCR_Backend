package app.ocr_backend.config

import app.ocr_backend.config.security.JwtAuthFilter
import app.ocr_backend.config.security.UserAuthProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(val userAuthProvider: UserAuthProvider) {


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
            .addFilterBefore(JwtAuthFilter(userAuthProvider),BasicAuthenticationFilter::class.java)
                .csrf()
                {
                    it.disable()
                }
                .authorizeHttpRequests()
                {
                    it.requestMatchers(HttpMethod.POST,"/login","/register").permitAll()
                    //TODO Remove in Production
                    it.requestMatchers(HttpMethod.GET,"/h2-console").permitAll()
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

