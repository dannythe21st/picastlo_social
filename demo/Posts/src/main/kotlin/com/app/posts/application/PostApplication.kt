package com.app.posts.application

import com.app.posts.config.filters.UserAuthToken
import com.app.posts.data.PostDAO
import com.app.posts.data.PostDTO
import com.app.posts.data.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class PostApplication(val repo: PostRepository) {

    fun publishPost(newPost: PostDAO) : PostDAO{
        return repo.save(newPost)
    }

    fun getUserFeed(page:Int,size:Int) : List<PostDAO>{
        val auth = SecurityContextHolder.getContext().authentication.principal
        val authToken = (auth as UserAuthToken)

        val pageable: Pageable = PageRequest.of(page, size)
        return repo.findAllByFriendsGroupsAndPublic(authToken.friendsList,authToken.groupsList,pageable).toList()
    }

    fun getUserPosts(username: String, page:Int,size:Int) : List<PostDAO>{
        val pageable: Pageable = PageRequest.of(page, size)
        return repo.findAllByUsername(username, pageable)
    }

    fun getMyPosts(username:String,page:Int,size:Int) : List<PostDAO>{
        val pageable: Pageable = PageRequest.of(page, size)
        return repo.findAllByUsername(username,pageable).toList()
    }

    fun getPost(id: Long): Optional<PostDAO>{
        return repo.findById(id)
    }

    fun getPublicPosts(page:Int,size:Int) : List<PostDAO>{
        val pageable: Pageable = PageRequest.of(page, size)
        return repo.findAllByPublic(pageable)
    }

    fun getGroupFeed(id: Long, page:Int, size:Int) : List<PostDAO>{
        val pageable: Pageable = PageRequest.of(page, size)
        return repo.findAllByGroup(id, pageable)
    }

}