package com.app.images.application

import com.app.images.data.ImageDAO
import com.app.images.data.ImageRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class ImageApplication(val repo: ImageRepository) {

    fun createImage(imageDAO : ImageDAO) : ImageDAO{
        return repo.save(imageDAO)
    }

    fun getUserImages(username: String): List<ImageDAO> {
        return repo.findAllByUserOwner(username)
    }

    fun getImage(imageID : Long): Optional<ImageDAO> {
        return repo.findById(imageID)
    }
    fun deleteImage(id: Long){
        repo.deleteById(id)
    }
}