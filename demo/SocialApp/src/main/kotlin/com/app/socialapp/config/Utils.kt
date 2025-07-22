package com.app.socialapp.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.Authentication
import java.util.*
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.SignatureException

@Configuration
class JWTUtils(@Value("\${jwt.secret}") val jwtSecret: String,
               @Value("\${jwt.expiration}") val expiration: Long,
               @Value("\${jwt.subject}") val subject: String) {


    val key = Base64.getEncoder().encodeToString(jwtSecret.toByteArray())

    fun addResponseToken(authentication: Authentication, response: HttpServletResponse) {

        val claims = HashMap<String, Any?>()
        claims["username"] = authentication.name


        val token = Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS256, key)
            .compact()

        response.addHeader("Authorization", "Bearer $token")
    }

    fun generateToken(username:String): String? {

        val claims = HashMap<String, Any?>()
        claims["username"] = username

        val token = Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS256, key)
            .compact()

        return token
    }

    fun validateToken(token: String): Boolean {
        return try {
            // Analisa e valida o token
            val claims: Jws<Claims> = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)

            val expirationDate = claims.body.expiration
            !expirationDate.before(Date())
        } catch (e: SignatureException) {
            false
        } catch (e: Exception) {
            false
        }
    }
    fun getUsernameFromToken(token: String): String? {
        return try {
            val claims: Jws<Claims> = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)

            claims.body["username"] as String
        } catch (e: Exception) {
            null
        }
    }


}

