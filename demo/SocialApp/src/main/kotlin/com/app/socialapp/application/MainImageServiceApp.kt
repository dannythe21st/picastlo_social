package com.app.socialapp.application

import com.app.socialapp.data.ImageDTO
import com.app.socialapp.service.ImageService
import feign.FeignException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@Component
class MainImageServiceApp(val imageService: ImageService) {

    fun getImage(imageID : Long): ImageDTO {
        return try{
            imageService.getImage(imageID)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun getUserImage(username : String): List<ImageDTO> {
        return try{
            imageService.getUserImages(username)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun getPublicImage(imageId : Long): ImageDTO {
        return try{
            imageService.getPublicImage(imageId)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun createImage(file: MultipartFile):ImageDTO{
        return try{
            imageService.createImage(file = file)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun deleteImage(id: Long){
        try{
            imageService.deleteImage(id)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun createImageFromText(imageBase64: String): ImageDTO {
        return try{
            imageService.createImageFromText(imageBase64 = imageBase64)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

}