package com.app.socialapp.presentation

import com.app.socialapp.data.PostDTO
import com.app.socialapp.data.PostPaginationDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("")
@Tag(name = "Posts", description = "Posts API")
interface MainPostServiceAPI {

    @GetMapping("/posts/{id}")
    @Operation(summary = "Get a post by its Id")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Post with requested id fetched successfully"),
        ApiResponse(responseCode = "400", description = "Invalid Id"),
        ApiResponse(responseCode = "404", description = "Post not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getPost(@PathVariable("id") id: Long): PostDTO

    @GetMapping("/public-feed")
    @Operation(summary = "Get all public posts")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Posts successfully fetched"),
        ApiResponse(responseCode = "400", description = "Invalid Id"),
        ApiResponse(responseCode = "404", description = "Posts not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getPublicPosts(@RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @GetMapping("/feed")
    @Operation(summary = "Get a user feed")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Feed fetched with success"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "403", description = "Unauthorized access"),
        ApiResponse(responseCode = "404", description = "Posts not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getUserFeed(@RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @GetMapping("/users/{username}/posts")
    @Operation(summary = "Get a users posts")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Feed fetched with success"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "403", description = "Unauthorized access"),
        ApiResponse(responseCode = "404", description = "Posts not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getUserPosts(@PathVariable("username") username: String,
                     @RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @GetMapping("/posts")
    @Operation(summary = "Get my own posts")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Posts fetched with success"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "403", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "Posts not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getMyPosts(@RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @GetMapping("/group/{groupId}")
    @Operation(summary = "Get group posts")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Returned groups posts"),
        ApiResponse(responseCode = "401", description = "Unauthorized Access"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getGroupFeed(@PathVariable("groupId") id: Long, @RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @PostMapping("/posts")
    @Operation(summary = "Publish a new post")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Post published with success"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "403", description = "Unauthorized"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun publishPost(@RequestBody newPost: PostDTO): PostDTO

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "Delete a post")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Post deleted with success"),
        ApiResponse(responseCode = "400", description = "Invalid ID"),
        ApiResponse(responseCode = "403", description = "Unauthorized"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun deletePost(@PathVariable("id") id: Long): Boolean

    @PutMapping("/posts/{id}")
    @Operation(summary = "Update a post")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Post updated with success"),
        ApiResponse(responseCode = "400", description = "Invalid ID"),
        ApiResponse(responseCode = "403", description = "Unauthorized"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun updatePost(@PathVariable("id") id: Long, @RequestBody updatedPost : PostDTO): PostDTO
}