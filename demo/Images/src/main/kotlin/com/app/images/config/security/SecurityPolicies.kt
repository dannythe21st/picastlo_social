package com.app.images.config.security

import com.app.images.config.filters.UserAuthToken
import com.app.images.data.ImageDTO
import com.app.images.data.ImageRepository
import com.app.images.data.Visibility
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.security.Principal

@Service
class capabilitiesService(val imageRepo: ImageRepository) {

    fun canUpdateOrDeleteImage(user: Principal,id:Long): Boolean {
        val username = (user as UserAuthToken).name
        val image = imageRepo.findById(id)

        if(image.isPresent){
            val i = image.get()
            return i.userOwner.equals(username)
        }
        return false
    }

    fun canReadImage(user:Principal,imageId:Long) : Boolean{
        val authToken = (user as UserAuthToken)
        val i = imageRepo.findById(imageId)
        if(i.isPresent){
            val image = i.get()
            return when (image.visibility) {
                Visibility.PUBLIC -> true
                Visibility.FRIEND_ONLY -> authToken.friendsList.contains(image.userOwner) || image.userOwner.equals(authToken.name)
                Visibility.GROUP -> authToken.groupsList.contains(image.groupId) || image.userOwner.equals(authToken.name)
                Visibility.PRIVATE -> image.userOwner.equals(authToken.name)
            }
        }
        return false
    }

    fun canReadMultipleImages(user:Principal,images:List<ImageDTO>): Boolean {
        val authToken = (user as UserAuthToken)
        return images.any { image ->
            when (image.visibility) {
                Visibility.PUBLIC -> true
                Visibility.FRIEND_ONLY -> authToken.friendsList.contains(image.userOwner) || image.userOwner.equals(authToken.name)
                Visibility.GROUP -> authToken.groupsList.contains(image.groupId) || image.userOwner.equals(authToken.name)
                Visibility.PRIVATE -> image.userOwner.equals(authToken.name)
            }
        }
    }
}

@PreAuthorize("@capabilitiesService.canUpdateOrDeleteImage(principal,#id)")
annotation class CanUpdateOrDeleteImage

@PreAuthorize("@capabilitiesService.canReadImage(principal,#id)")
annotation class CanReadImage

@PostFilter("@capabilitiesService.canReadMultipleImages(principal, filterObject)")
annotation class CanReadMultipleImages
