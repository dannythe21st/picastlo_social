package com.app.posts.presentation

import com.app.posts.config.security.*
import com.app.posts.data.PostDTO
import com.app.posts.data.PostPaginationDTO
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/post")
interface PostAPI {

    @GetMapping("/{id}")
    @CanViewPost
    fun getPost(@PathVariable("id") id: Long): PostDTO

    @GetMapping("/public")
    fun getPublicPosts(@RequestParam("page") page: Int,@RequestParam("size") size: Int): PostPaginationDTO

    @GetMapping("/feed")
    //@CanViewMultiplePosts
    fun getUserFeed(@RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @GetMapping("/group/{id}")
    fun getGroupFeed(@PathVariable("id") id: Long, @RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @GetMapping("/personal")
    //@CanViewMyPosts
    fun getMyPosts(@RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @GetMapping("")
    fun getUserPosts(@RequestParam("username", required = true) username: String,
                     @RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @PostMapping("")
    @CanPublish
    fun publishPost(@RequestBody newPost: PostDTO): PostDTO

    @DeleteMapping("/{id}")
    @CanUpdateOrDeletePost
    fun deletePost(@PathVariable("id") id: Long): Boolean

    @PutMapping("/{id}")
    @CanUpdateOrDeletePost
    fun updatePost(@PathVariable("id") id: Long, @RequestBody updatedPost : PostDTO): PostDTO
}