package com.app.images.presentation

import com.app.images.config.security.CanReadImage
import com.app.images.config.security.CanReadMultipleImages
import com.app.images.config.security.CanUpdateOrDeleteImage
import com.app.images.data.ImageDTO
import com.app.images.data.Visibility
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/images")
interface ImageAPI {

    @GetMapping("/{id}")
    @CanReadImage
    fun getImage(@PathVariable("id") id: Long): ImageDTO

    @GetMapping("/public/{id}")
    fun getPublicImage(@PathVariable("id") id: Long): ImageDTO

    @GetMapping("/{username}/album")
    @CanReadMultipleImages
    fun getUserImages(@PathVariable("username") username: String): List<ImageDTO>

    @PostMapping("/base64", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createImageFromText(@RequestPart("imageBase64") imageBase64: String): ImageDTO


    @PostMapping("", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createImage(@RequestPart("file") file: MultipartFile):ImageDTO

    @PutMapping("/{id}")
    @CanUpdateOrDeleteImage
    fun updateImageVisibility(@PathVariable("id") id: Long,
                              @RequestParam("visibility") visibility:Visibility,
                              @RequestParam("groupid") groupId:Long):ImageDTO

    @DeleteMapping("/{id}")
    @CanUpdateOrDeleteImage
    fun deleteImage(@PathVariable("id") id: Long)

}

