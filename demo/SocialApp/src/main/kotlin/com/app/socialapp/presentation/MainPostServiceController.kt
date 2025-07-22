package com.app.socialapp.presentation

import com.app.socialapp.application.MainPostServiceApp
import com.app.socialapp.data.PostDTO
import com.app.socialapp.data.PostPaginationDTO
import org.springframework.web.bind.annotation.RestController

@RestController
class MainPostServiceController (val app: MainPostServiceApp): MainPostServiceAPI{
    override fun getPost(id: Long): PostDTO {
        return app.getPost(id)
    }

    override fun getPublicPosts(page: Int, size: Int): PostPaginationDTO {
        return app.getPublicPosts(page, size)
    }

    override fun getUserFeed(page: Int, size: Int): PostPaginationDTO {
        return app.getUserFeed(page,size)
    }

    override fun getUserPosts(username: String, page: Int, size: Int): PostPaginationDTO {
        return app.getUserPosts(username, page, size)
    }

    override fun getMyPosts(page: Int, size: Int): PostPaginationDTO {
        return app.getMyPosts(page,size)
    }

    override fun getGroupFeed(id: Long, page: Int, size: Int): PostPaginationDTO {
        return app.getGroupFeed(id,page,size)
    }


    override fun publishPost(newPost: PostDTO): PostDTO {
        return app.publishPost(newPost)
    }

    override fun deletePost(id: Long): Boolean {
        return app.deletePost(id)
    }

    override fun updatePost(id: Long, updatedPost: PostDTO): PostDTO {
        return app.updatePost(id, updatedPost)
    }


}