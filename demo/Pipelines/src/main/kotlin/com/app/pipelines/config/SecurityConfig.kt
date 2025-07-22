package com.app.pipelines.config

import com.app.pipelines.config.filters.JWTAuthenticationFilter
import com.app.pipelines.config.filters.JWTUtils
import com.app.pipelines.config.filters.UserPasswordAuthenticationFilterToJWT
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
open class SecurityConfig {

    @Bean
    @Throws(Exception::class)
    open fun securityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager,
        utils: JWTUtils
    ): SecurityFilterChain {
        http.invoke {
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/pipeline/public/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            addFilterBefore<BasicAuthenticationFilter>(UserPasswordAuthenticationFilterToJWT("/login", authenticationManager, utils))
            addFilterBefore<BasicAuthenticationFilter>(JWTAuthenticationFilter(utils))
            httpBasic { }
        }
        return http.build()
    }

    // To declare only one user
    @Bean
    fun userDetailsService(): UserDetailsService {
        val userDetails = User.builder()
            .username("admin")
            .password(BCryptPasswordEncoder().encode("admin"))
            .roles("ADMIN")
            .build()
        return InMemoryUserDetailsManager(userDetails)
    }

    @Bean
    fun authenticationManager(authConfiguration: AuthenticationConfiguration): AuthenticationManager? {
        return authConfiguration.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
