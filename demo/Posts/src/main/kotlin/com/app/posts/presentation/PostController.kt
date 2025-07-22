package com.app.posts.presentation

import com.app.posts.application.PostApplication
import com.app.posts.config.filters.UserAuthToken
import com.app.posts.data.PostDAO
import com.app.posts.data.PostDTO
import com.app.posts.data.PostPaginationDTO
import com.app.posts.data.Visibility
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import com.app.posts.config.security.CanViewMultiplePosts

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidRequest() : RuntimeException("Invalid request")

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class NotAMemberException() : RuntimeException("You are not a member of this group!")

@RestController
class PostController(val app: PostApplication) : PostAPI {
    private val logger: Logger = LoggerFactory.getLogger(PostController::class.java)

    override fun getPost(id: Long): PostDTO {
        logger.info("getPost")
        if(id<0){
            throw InvalidRequest()
        }

        val post = app.getPost(id).get()

        return PostDTO(post.id,post.username, post.image, post.pipeline, post.groupId,post.text,post.visibility, post.createdAt)
    }

    override fun getPublicPosts(page:Int,size:Int): PostPaginationDTO {
        logger.info("getPost")
        val totalPosts = app.repo.countAllByPublic()
        val allPosts = app.getPublicPosts(page,size)
        val tmp = allPosts.map { post ->
            PostDTO(
                id = post.id,
                user = post.username,
                image = post.image,
                pipeline = post.pipeline,
                groupId = post.groupId,
                text = post.text,
                visibility = post.visibility,
                createdAt = post.createdAt,
            )
        }
        return PostPaginationDTO(tmp,totalPosts)
    }

    override fun getUserFeed(page: Int, size: Int): PostPaginationDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        val authToken = (authentication as UserAuthToken)
        val totalPosts = app.repo.countAllFeedPosts(authToken.friendsList,authToken.groupsList)
        val allPosts = app.getUserFeed(page, size)
        val tmp = allPosts.map { post ->
            PostDTO(
                id = post.id,
                user = post.username,
                image = post.image,
                pipeline = post.pipeline,
                groupId = post.groupId,
                text = post.text,
                visibility = post.visibility,
                createdAt = post.createdAt,
            )
        }

        val filteredPosts = tmp.filter { post ->
            when (post.visibility) {
                Visibility.PUBLIC -> true
                Visibility.FRIEND_ONLY -> authToken.friendsList.contains(post.user) || post.user == authToken.name
                Visibility.GROUP -> authToken.groupsList.contains(post.groupId)
            }
        }
        return PostPaginationDTO(filteredPosts, totalPosts)
    }

    override fun getGroupFeed(id: Long,page:Int,size:Int): PostPaginationDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        val authToken = (authentication as UserAuthToken)

        if(authToken.groupsList.contains(id)){
            val totalPosts = app.repo.countAllByGroup(id)
            val allPosts = app.getGroupFeed(id, page, size)

            val tmp = allPosts.map {
                    post ->
                PostDTO(
                    id = post.id,
                    user = post.username,
                    image = post.image,
                    pipeline = post.pipeline,
                    groupId = post.groupId,
                    text = post.text,
                    visibility = post.visibility,
                    createdAt = post.createdAt,
                )
            }
            return PostPaginationDTO(tmp,totalPosts)
        }
        throw NotAMemberException()
    }

    override fun getMyPosts(page:Int,size:Int): PostPaginationDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        val totalPosts = app.repo.countAllByUsername(authentication.name)
        val allPosts = app.getMyPosts(authentication.name,page,size);

        val tmp = allPosts.map {
            post ->
                PostDTO(
                    id = post.id,
                    user = post.username,
                    image = post.image,
                    pipeline = post.pipeline,
                    groupId = post.groupId,
                    text = post.text,
                    visibility = post.visibility,
                    createdAt = post.createdAt,
                )
        }

        val filteredPosts = tmp.filter { post ->
            post.user == authentication.name
        }
        return PostPaginationDTO(filteredPosts,totalPosts)
    }

    override fun getUserPosts(username: String, page: Int, size: Int): PostPaginationDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        val authToken = (authentication as UserAuthToken)
        val allPosts = app.getUserPosts(username, page, size)

        val tmp = allPosts.map { post ->
            PostDTO(
                id = post.id,
                user = post.username,
                image = post.image,
                pipeline = post.pipeline,
                groupId = post.groupId,
                text = post.text,
                visibility = post.visibility,
                createdAt = post.createdAt,
            )
        }

        val filteredPosts = tmp.filter { post ->
            when (post.visibility) {
                Visibility.PUBLIC -> true
                Visibility.FRIEND_ONLY -> authToken.friendsList.contains(post.user)
                Visibility.GROUP -> authToken.groupsList.contains(post.groupId)
            }
        }

        val totalPosts = filteredPosts.size

        return PostPaginationDTO(filteredPosts, totalPosts)
    }

    override fun publishPost(newPost: PostDTO): PostDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        val authToken = (authentication as UserAuthToken)
        val userGroups = authToken.groupsList

        if(newPost.user.isBlank() || newPost.image < 0 || newPost.pipeline < 0 ||
            (newPost.groupId<=0 && newPost.visibility == Visibility.GROUP)){
            throw InvalidRequest()
        }

        if (!userGroups.contains(newPost.groupId)){
            throw NotAMemberException()
        }

        val postd = PostDAO(0,newPost.user,newPost.image,newPost.pipeline,newPost.text,newPost.groupId,newPost.visibility)
        val post = app.publishPost(postd)

        return PostDTO(post.id,post.username, post.image, post.pipeline, post.groupId, post.text,post.visibility, post.createdAt)
    }

    override fun deletePost(id: Long): Boolean {
        TODO("Not to implement")
    }

    override fun updatePost(id: Long, updatedPost: PostDTO): PostDTO {
        TODO("Not to implemented")
    }
}

