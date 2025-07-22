package com.app.socialapp.presentation

import com.app.socialapp.application.MainImageServiceApp
import com.app.socialapp.data.ImageDTO
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class MainImageServiceController(val app: MainImageServiceApp): MainImageServiceAPI {
    override fun getImage(id: Long): ImageDTO {
        return app.getImage(id)
    }

    override fun getUserImages(username: String): List<ImageDTO> {
        return app.getUserImage(username)
    }

    override fun createImageFromText(imageBase64: String): ImageDTO {
        return app.createImageFromText(imageBase64)
    }

    override fun getPublicImage(id: Long): ImageDTO {
        return app.getPublicImage(id)
    }

    override fun createImage(file: MultipartFile): ImageDTO {
        return app.createImage(file)
    }

    override fun deleteImage(id: Long) {
        return app.deleteImage(id)
    }
}