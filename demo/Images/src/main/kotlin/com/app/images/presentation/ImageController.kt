package com.app.images.presentation

import com.app.images.application.ImageApplication
import com.app.images.data.ImageDAO
import com.app.images.data.ImageDTO
import com.app.images.data.Visibility
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

@ResponseStatus(HttpStatus.NOT_FOUND)
class ImageNotFoundException() : RuntimeException("Image not found")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidRequest() : RuntimeException("Invalid request")

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class CantSeeImageException(): RuntimeException("Log in to see images that are not public")

@RestController
class ImageController(val app: ImageApplication ): ImageAPI{
    private val logger: Logger = LoggerFactory.getLogger(ImageController::class.java)

    override fun getImage(id: Long): ImageDTO {
        logger.info("getImage")
        if(id<0){
            throw InvalidRequest()
        }

        val image = app.getImage(id)

        if (!image.isPresent)
            throw ImageNotFoundException()

        val imageAux = image.get()
        return ImageDTO(imageAux.id,imageAux.image, imageAux.userOwner, imageAux.visibility, imageAux.groupId)
    }

    override fun getUserImages(username: String): List<ImageDTO> {
        val userImages = app.getUserImages(username)
        var mutableList = mutableListOf<ImageDTO>()
        for(imageTmp in userImages){
            mutableList.add(
                ImageDTO(
                    imageTmp.id,
                    imageTmp.image,
                    imageTmp.userOwner,
                    imageTmp.visibility,
                    imageTmp.groupId
            ))
        }
        return mutableList
    }

    override fun getPublicImage(imageID : Long): ImageDTO {
        if(imageID<0){
            throw InvalidRequest()
        }

        val image = app.getImage(imageID)

        if (!image.isPresent)
            throw ImageNotFoundException()

        val imageAux = image.get()

        if(imageAux.visibility != Visibility.PUBLIC)
            throw CantSeeImageException()

        return ImageDTO(imageAux.id,imageAux.image, imageAux.userOwner, imageAux.visibility, imageAux.groupId)
    }

    override fun createImage(file: MultipartFile): ImageDTO {
        if (file.isEmpty)
            throw InvalidRequest()

        val originalImage = file.bytes

        val userOwner = SecurityContextHolder.getContext().authentication.name

        val imageDAO = ImageDAO(
            id=0,
            image = originalImage,
            userOwner = userOwner,
            visibility = Visibility.PUBLIC,
            groupId = -1,
            createdAt = Instant.now()
        )

        val savedImage = app.createImage(imageDAO)

        return ImageDTO(
            savedImage.id,
            savedImage.image,
            savedImage.userOwner,
            savedImage.visibility,
            savedImage.groupId
        )
    }

    override fun createImageFromText(imageBase64: String): ImageDTO {
        if (imageBase64.isBlank()) {
            throw InvalidRequest()
        }

        val decodedImage: ByteArray
        try {
            decodedImage = Base64.getDecoder().decode(imageBase64)
        } catch (e: IllegalArgumentException) {
            logger.error("Invalid Base64 format", e)
            throw InvalidRequest()
        }

        val userOwner = SecurityContextHolder.getContext().authentication.name

        val imageDAO = ImageDAO(
            id = 0,
            image = decodedImage,
            userOwner = userOwner,
            visibility = Visibility.PUBLIC,
            groupId = -1,
            createdAt = Instant.now()
        )

        val savedImage = app.createImage(imageDAO)

        return ImageDTO(
            savedImage.id,
            savedImage.image,
            savedImage.userOwner,
            savedImage.visibility,
            savedImage.groupId
        )
    }


    override fun updateImageVisibility(id: Long, visibility: Visibility, groupId: Long): ImageDTO {
        if(id<0)
            throw InvalidRequest()

        val image = app.getImage(id)

        if (!image.isPresent)
            throw ImageNotFoundException()

        val dao = image.get()
        dao.visibility = visibility
        dao.groupId = groupId
        app.createImage(dao)
        return ImageDTO(dao.id, dao.image, dao.userOwner, dao.visibility, dao.groupId)
    }


    override fun deleteImage(id: Long) {
        if(id<0)
            throw InvalidRequest()

        val image = app.getImage(id)

        if (!image.isPresent)
            throw ImageNotFoundException()

        app.deleteImage(id)
    }
}