package com.app.socialapp.presentation

import com.app.socialapp.data.ImageDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RequestMapping("")
@Tag(name = "Images", description = "Images API")
interface MainImageServiceAPI {

    @GetMapping("/images/{id}")
    @Operation(summary = "Get an image by Id")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Image with requested id was found"),
        ApiResponse(responseCode = "400", description = "Invalid Request - id field is blank"),
        ApiResponse(responseCode = "403", description = "User can't access this image"),
        ApiResponse(responseCode = "404", description = "Image not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getImage(@PathVariable("id") id: Long): ImageDTO


    @GetMapping("/public-images/{id}")
    @Operation(summary = "Get an public image by Id - to unauthenticated users")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Image with requested id was found"),
        ApiResponse(responseCode = "400", description = "Invalid Request - id field is blank"),
        ApiResponse(responseCode = "403", description = "User can't access this image"),
        ApiResponse(responseCode = "404", description = "Image not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getPublicImage(@PathVariable("id") id: Long): ImageDTO

    @GetMapping("/users/{username}/images")
    @Operation(summary = "Get all the images posted by a user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User images were returned"),
        ApiResponse(responseCode = "400", description = "Invalid Request"),
        ApiResponse(responseCode = "403", description = "User can't access these images"),
        ApiResponse(responseCode = "404", description = "Images not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getUserImages(@PathVariable("username") username: String): List<ImageDTO>
    
    @PostMapping("/images/base64", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Post an Image from text")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Image created from text posted successfully"),
        ApiResponse(responseCode = "400", description = "Invalid Request"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun createImageFromText(@RequestPart("imageBase64") imageBase64: String): ImageDTO


    @PostMapping("/images" , consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Post an image")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Image posted successfully"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun createImage(@RequestPart("file") file: MultipartFile):ImageDTO

    @DeleteMapping("/images/{id}")
    @Operation(summary = "Delete an image")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Image deleted successfully"),
        ApiResponse(responseCode = "400", description = "Invalid Request - ID field is blank"),
        ApiResponse(responseCode = "403", description = "User can't delete this image"),
        ApiResponse(responseCode = "404", description = "Image not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun deleteImage(@PathVariable("id") id: Long)

}