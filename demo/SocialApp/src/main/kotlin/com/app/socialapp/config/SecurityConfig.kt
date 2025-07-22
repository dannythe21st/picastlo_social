package com.app.socialapp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
// check: https://docs.spring.io/spring-security/reference/servlet/getting-started.html

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
open class SecurityConfig {

    @Bean
    @Throws(Exception::class)
    open fun securityFilterChain(
        http: HttpSecurity,
        utils: JWTUtils,
        jwtAuthenticationFilter: JWTAuthenticationFilter
    ): SecurityFilterChain {
        http.invoke {
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/swagger-ui/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/users", permitAll)
                authorize("/login", permitAll)
                authorize("/public-feed**", permitAll)
                authorize("/public-images**", permitAll)
                authorize("/public-pipelines/**", permitAll)
                authorize(anyRequest, authenticated)
            }

            addFilterBefore<BasicAuthenticationFilter>(jwtAuthenticationFilter)
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}

