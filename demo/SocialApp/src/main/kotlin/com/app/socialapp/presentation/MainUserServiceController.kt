package com.app.socialapp.presentation

import com.app.socialapp.application.MainUserServiceApp
import com.app.socialapp.config.JWTUtils
import com.app.socialapp.data.*
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException() : RuntimeException("User not found")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidRequest() : RuntimeException("Invalid request")

@ResponseStatus(HttpStatus.CONFLICT)
class UserAlreadyExistsException() : RuntimeException("This user is already registered.")


@RestController
class MainUserServiceController(val app: MainUserServiceApp, val jwtUtils: JWTUtils): MainUserServiceAPI{
    private val logger: Logger = LoggerFactory.getLogger(MainUserServiceController::class.java)

    override fun getUser(email: String): UserDTO {
        if(email.isBlank()){
            throw InvalidRequest()
        }

        val user = app.findUser(email)

        if (!user.isPresent)
            throw UserNotFoundException()

        var u = user.get()
        return UserDTO(u.username)

    }

    override fun getUsers(page: Int, size: Int): UsersPaginationDTO {
        val totalUsers = app.repo.countAllUsers()
        val allUsers = app.getAllUsers(page, size).distinct()

        val tmp = allUsers.map { user ->
            UserDTO(
                username = user.username
            )
        }
        return UsersPaginationDTO(tmp,totalUsers)
    }

    override fun createUser(user: CreateUserDTO): UserDTO {
        if(user.username.isBlank() || user.password.isBlank() || user.username == "anonymousUser"){
            throw InvalidRequest()
        }

        if (app.findUser(user.username).isPresent)
            throw UserAlreadyExistsException()

        val u = app.createUser(UserDAO(user.username,BCryptPasswordEncoder().encode(user.password)))

        return UserDTO(u.username)
    }

    override fun loginUser(user: LoginUserDTO): ResponseEntity<Any> {
        if(user.username.isBlank() || user.password.isBlank() || user.username == "anonymousUser"){
            throw InvalidRequest()
        }

        val userDAO = app.findUser(user.username)

        if (!userDAO.isPresent)
            return ResponseEntity("Invalid credentials", HttpStatus.NOT_FOUND)

        val encoder = BCryptPasswordEncoder()

        if (encoder.matches(user.password, userDAO.get().password)){
            val headers = HttpHeaders()

            val token = jwtUtils.generateToken(user.username)

            headers.set("Authorization", "Bearer $token")

            return ResponseEntity(true, headers, HttpStatus.OK)
        }
        return ResponseEntity("Invalid credentials", HttpStatus.UNAUTHORIZED)
    }


}