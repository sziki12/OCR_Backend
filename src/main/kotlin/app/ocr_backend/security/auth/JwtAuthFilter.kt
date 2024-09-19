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
                try {
                    SecurityContextHolder.getContext().authentication = userAuthProvider.validateToken(elements[1])
                }
                catch (e:RuntimeException)
                {
                    val optUser = userAuthProvider.getUserByToken(elements[1])
                    SecurityContextHolder.clearContext()
                    System.err.println("Token expired for user: ${if(optUser.isPresent)optUser.get().toString() else "Unknown"}")
                }
            }
        }
        filterChain.doFilter(request,response)
    }
}