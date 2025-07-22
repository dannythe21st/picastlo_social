package com.app.posts.config.filters

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

data class UserAuthToken(
    private val login: String,
    private val authorities: List<GrantedAuthority>,
    val friendsList: List<String>,
    val groupsList: List<Long>
) : Authentication {

    override fun getAuthorities() = authorities

    override fun setAuthenticated(isAuthenticated: Boolean) {}

    override fun getName() = login

    override fun getCredentials() = null

    override fun getPrincipal() = this

    override fun isAuthenticated() = true

    override fun getDetails() = login
}

class JWTAuthenticationFilter(val utils: JWTUtils) : GenericFilterBean() {

    val logger: Logger = LoggerFactory.getLogger(JWTAuthenticationFilter::class.java)

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val authHeader = (request as HttpServletRequest).getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7) // Pular "Bearer "
            try {
                val claims = Jwts.parser().setSigningKey(utils.key).parseClaimsJws(token).body

                val friendsList = (claims["friendsList"] as List<*>).map { it.toString() }
                val groupsList = (claims["groupsList"] as List<*>).map { it.toString().toLong() }

                logger.info("Registered friendsList: $friendsList")
                logger.info("Registered groupsList: $groupsList")

                val authentication = UserAuthToken(
                    claims["username"] as String,
                    listOf(SimpleGrantedAuthority("ROLE_USER")),
                    friendsList,
                    groupsList
                )

                SecurityContextHolder.getContext().authentication = authentication

                utils.addResponseToken(authentication, response as HttpServletResponse)

                chain!!.doFilter(request, response)
            } catch (e: ExpiredJwtException) {
                (response as HttpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED)
            }
        } else {
            chain!!.doFilter(request, response)
        }
    }
}
