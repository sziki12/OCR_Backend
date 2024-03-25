package app.ocr_backend.config.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter


class JwtAuthFilter(val userAuthProvider:UserAuthProvider):OncePerRequestFilter() {
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
                    SecurityContextHolder.clearContext()
                    throw e
                }
            }
        }
        filterChain.doFilter(request,response)
    }
}