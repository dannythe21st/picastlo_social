package com.app.socialapp.presentation
import com.app.socialapp.data.*
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@RequestMapping("")
@Tag(name = "Users", description = "Users API")
interface MainUserServiceAPI {

    @GetMapping("/users/{username}")
    @Operation(summary = "Get a user by Id")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User with requested id"),
        ApiResponse(responseCode = "400", description = "Invalid Request - Email field is blank"),
        ApiResponse(responseCode = "404", description = "User not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getUser(@PathVariable("username") email: String): UserDTO


    @GetMapping("/users")
    @Operation(summary = "Get all users")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of all users returned successfully"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getUsers(@RequestParam("page") page: Int,@RequestParam("size") size: Int): UsersPaginationDTO

    @PostMapping("/users")
    @Operation(summary = "Create a user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User created successfully "),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun createUser(@RequestBody user: CreateUserDTO): UserDTO

    @PostMapping("/login")
    @Operation(summary = "Login as a user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Correct credentials - Access granted"),
        ApiResponse(responseCode = "401", description = "Unauthorized access"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun loginUser(@RequestBody user: LoginUserDTO): ResponseEntity<Any>

}