package com.app.socialapp.application

import com.app.socialapp.data.PostDTO
import com.app.socialapp.data.PostPaginationDTO
import com.app.socialapp.service.PostService
import feign.FeignException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class MainPostServiceApp(val postService: PostService) {

    fun getPost(id : Long): PostDTO {
        return try{
            postService.getPost(id)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun getPublicPosts(page:Int,size:Int): PostPaginationDTO {
        return try{
            postService.getPublicPosts(page, size)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun getUserFeed(page:Int,size:Int): PostPaginationDTO {
        return try{
            postService.getUserFeed(page= page, size= size)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun getMyPosts(page:Int,size:Int): PostPaginationDTO{
        return try{
            postService.getMyPosts(page= page, size= size)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun getUserPosts(username:String, page:Int,size:Int): PostPaginationDTO{
        return try{
            postService.getUserPosts(username = username, page= page, size= size)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun publishPost(newPost: PostDTO): PostDTO {
        return try{
            postService.publishPost(newPost);
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun deletePost(id: Long): Boolean {
        return try{
            postService.deletePost(id);
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun updatePost(id: Long, updatedPost: PostDTO): PostDTO {
        return try{
            postService.updatePost(id, updatedPost);
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun getGroupFeed(id: Long, page: Int, size: Int): PostPaginationDTO {
        return try{
            postService.getGroupFeed(id,page,size)
        } catch (ex: FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }
}
