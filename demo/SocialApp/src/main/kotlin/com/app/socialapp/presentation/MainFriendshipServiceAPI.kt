package com.app.socialapp.presentation

import com.app.socialapp.data.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("")
@Tag(name = "Friends", description = "Friends API")
interface MainFriendshipServiceAPI {

    @PostMapping("/friends/{username}")
    @Operation(summary = "Add a new friend")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Friend successfully added"),
        ApiResponse(responseCode = "400", description = "Invalid Request - username is blank"),
        ApiResponse(responseCode = "404", description = "User not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun addFriend(@PathVariable("username") username: String): Boolean

    @DeleteMapping("/friends/{username}")
    @Operation(summary = "Remove a user from friends list")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Friend removed successfully"),
        ApiResponse(responseCode = "400", description = "Invalid Request - username is blank"),
        ApiResponse(responseCode = "404", description = "User not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun removeFriend(@PathVariable("username") username: String): Boolean

    @GetMapping("/users/{username}/friends")
    @Operation(summary = "Get friend list")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Friends list for the given user found"),
        ApiResponse(responseCode = "400", description = "Invalid Request - username is blank"),
        ApiResponse(responseCode = "404", description = "User not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getFriendsList(@PathVariable("username") username: String): List<String>

}