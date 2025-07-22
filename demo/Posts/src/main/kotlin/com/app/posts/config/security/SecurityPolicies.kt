package com.app.posts.config.security


import com.app.posts.config.filters.UserAuthToken
import com.app.posts.data.*
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.security.Principal


@Service
class capabilitiesService(val postRepo: PostRepository) {

    private val logger = LoggerFactory.getLogger(capabilitiesService::class.java)

    fun canUpdateOrDeletePost(user: Principal, id:Long): Boolean {
        val username = (user as UserAuthToken).name
        val post = postRepo.findById(id)

        if(post.isPresent){
            val p = post.get()
            return p.username.equals(username)
        }
        return false
    }

    /*fun canViewFriendOnly(user: Principal, post: PostDAO): Boolean {
        val authToken = (user as UserAuthToken)
        return post.user == authToken.name || authToken.friendsList.contains(post.user)
    }

    fun canViewGroup(user: Principal, post: PostDAO): Boolean {
        val authToken = user as UserAuthToken
        return post.visibility == Visibility.PUBLIC ||
                authToken.groupsList.contains(post.groupId) ||
                (post.visibility == Visibility.FRIEND_ONLY && authToken.friendsList.contains(post.user)) // amigo
    }*/

    fun canViewMultiplePosts(user: Principal, posts: PostPaginationDTO): Boolean {
        val authToken = (user as UserAuthToken)
        return posts.list.any { post ->
            when (post.visibility) {
                Visibility.PUBLIC -> true
                Visibility.FRIEND_ONLY -> authToken.friendsList.contains(post.user) || post.user.equals(authToken.name)
                Visibility.GROUP -> authToken.groupsList.contains(post.groupId)
            }
        }
    }

    fun canViewMyPosts(user: Principal, posts: PostPaginationDTO): Boolean {
        val authToken = (user as UserAuthToken)
        return posts.list.any { post ->
            (post.user.equals(authToken.name))
        }
    }

    fun canPublish(user: Principal, newPost:PostDTO): Boolean {
        val authToken = (user as UserAuthToken)

        return when (newPost.visibility) {
            Visibility.PUBLIC -> true
            Visibility.FRIEND_ONLY -> true
            Visibility.GROUP -> authToken.groupsList.contains(newPost.groupId)
        }
    }

    fun canViewPost(user:Principal,postId:Long) : Boolean{
        val authToken = (user as UserAuthToken)
        val p = postRepo.findById(postId)
        if(p.isPresent){
            val post = p.get()
            return when (post.visibility) {
                Visibility.PUBLIC -> true
                Visibility.FRIEND_ONLY -> authToken.friendsList.contains(post.username)
                        || post.username.equals(authToken.name)
                Visibility.GROUP -> authToken.groupsList.contains(post.groupId)
            }
        }
        return false
    }

}

@PreAuthorize("@capabilitiesService.canUpdateOrDeletePost(principal,#id)")
annotation class CanUpdateOrDeletePost

@PreAuthorize("@capabilitiesService.canPublish(principal,#newPost)")
annotation class CanPublish

@PostFilter("@capabilitiesService.canViewMyPosts(principal,filterObject)")
annotation class CanViewMyPosts

@PreAuthorize("@capabilitiesService.canViewPost(principal,#id)")
annotation class CanViewPost

@PostFilter("@capabilitiesService.canViewMultiplePosts(principal, filterObject)")
annotation class CanViewMultiplePosts