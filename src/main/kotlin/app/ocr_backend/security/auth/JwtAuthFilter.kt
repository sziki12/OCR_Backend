package app.ocr_backend.security.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter


class JwtAuthFilter(private val userAuthProvider:UserAuthProvider):OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        header?.let {
            val elements = it.split(" ")
            if(elements.size == 2 &&elements[0] == "Bearer")
            {
                val token = elements[1]
                try {
                    SecurityContextHolder.getContext().authentication = userAuthProvider.validateToken(token)
                }
                catch (e:RuntimeException)
                {
                    val optUser = userAuthProvider.getUserByToken(token)
                    SecurityContextHolder.clearContext()

                    System.err.println("Token $token expired for user: ${if(optUser.isPresent)optUser.get().id else "Unknown"}")
                }
            }
        }
        filterChain.doFilter(request,response)
    }
}