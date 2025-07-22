package com.app.socialapp.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.web.filter.GenericFilterBean
import java.nio.charset.StandardCharsets
import java.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

object JWTSecret {
    private const val passphrase = "este é um grande segredo que tem que ser mantido escondido"
    val KEY: String = Base64.getEncoder().encodeToString(passphrase.toByteArray())
    const val SUBJECT = "JSON Web Token for IADI 2022/2023"
    const val VALIDITY = 1000 * 60 * 10 // 10 minutes in milliseconds
}


private fun addResponseToken(authentication: Authentication, response: HttpServletResponse) {

    val claims = HashMap<String, Any?>()
    claims["username"] = authentication.name
    claims["roles"] = "USER"

    val token = Jwts
        .builder()
        .setClaims(claims)
        .setSubject(JWTSecret.SUBJECT)
        .setIssuedAt(Date(System.currentTimeMillis()))
        .setExpiration(Date(System.currentTimeMillis() + JWTSecret.VALIDITY))
        .signWith(SignatureAlgorithm.HS256, JWTSecret.KEY)
        .compact()

    response.addHeader("Authorization", "Bearer $token")
}

data class UserLogin(var username:String, var password:String) {
    constructor() : this("","")
}


class UserPasswordAuthenticationFilterToJWT (
    defaultFilterProcessesUrl: String?,
    private val anAuthenticationManager: AuthenticationManager?,
    val utils: JWTUtils
) : AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl) {


    override fun attemptAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ): Authentication? {
        val body = String(request!!.inputStream.readAllBytes(), StandardCharsets.UTF_8)

        val user = ObjectMapper().readValue(body, UserLogin::class.java)
        //println("User extracted: ${user.username}")

        val auth = anAuthenticationManager?.authenticate(
            UsernamePasswordAuthenticationToken("rcosta", "12345")
        )

        if (auth?.isAuthenticated == true) {
            println("Authentication successful for user: ${user.username}")
            SecurityContextHolder.getContext().authentication = auth
            return auth
        } else {
            println("Authentication failed for user: ${user.username}")
            return null
        }
    }


    override fun successfulAuthentication(request: HttpServletRequest,
                                          response: HttpServletResponse,
                                          filterChain: FilterChain?,
                                          auth: Authentication) {

        // When returning from the Filter loop, add the token to the response
        println("Successful authentication for user: ${auth.name}")

        utils.addResponseToken(auth, response)
    }
}

class UserAuthToken(private val login:String, private val authorities:List<GrantedAuthority>) : Authentication {

    override fun getAuthorities() = authorities

    override fun setAuthenticated(isAuthenticated: Boolean) {}

    override fun getName() = login

    override fun getCredentials() = null

    override fun getPrincipal() = this

    override fun isAuthenticated() = true

    override fun getDetails() = login
}

@Component
class JWTAuthenticationFilter(
    private val utils: JWTUtils
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = extractToken(request)

        if(token!=null && utils.validateToken(token)){
            val username = utils.getUsernameFromToken(token)
            //val username = "rcosta"
            val authentication = UsernamePasswordAuthenticationToken(username, null,null)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization")
        return if (header != null && header.startsWith("Bearer ")) {
            header.substring(7)
        } else null
    }
}
