package com.app.socialapp.service

import com.app.socialapp.data.*
import feign.Logger
import feign.RequestInterceptor
import feign.codec.ErrorDecoder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import java.util.*

@FeignClient("PostsServices", configuration = [PostServiceConfig::class],fallbackFactory = PostFallbackFactory::class)
interface PostService {

    @GetMapping("/post/{id}")
    fun getPost(@PathVariable("id") id: Long): PostDTO

    @GetMapping("/post/public")
    fun getPublicPosts(@RequestParam("page") page: Int,@RequestParam("size") size: Int): PostPaginationDTO

    @GetMapping("/post/feed")
    fun getUserFeed(@RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @GetMapping("/post")
    fun getUserPosts(@RequestParam("username", required = true) username: String,
                     @RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @GetMapping("/post/personal")
    fun getMyPosts(@RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @GetMapping("/post/group/{id}")
    fun getGroupFeed(@PathVariable("id") id: Long, @RequestParam("page") page: Int,@RequestParam("size") size: Int) : PostPaginationDTO

    @PostMapping("/post")
    fun publishPost(@RequestBody newPost: PostDTO): PostDTO

    @DeleteMapping("/post/{id}")
    fun deletePost(@PathVariable("id") id: Long): Boolean

    @PutMapping("/post/{id}")
    fun updatePost(@PathVariable("id") id: Long, @RequestBody updatedPost : PostDTO): PostDTO
}

@Configuration
class PostServiceConfig(
    @Value("\${jwt.secret}") val jwtSecret: String,
    @Value("\${jwt.expiration}") val expiration: Long,
    @Value("\${jwt.subject}") val subject: String,
    val friendsRepo: FriendsRepository,
    val userRepo: UserRepository
) {

    val logger: org.slf4j.Logger = LoggerFactory.getLogger(PostServiceConfig::class.java)

    @Bean
    fun postAPIInterceptor(): RequestInterceptor {
        return RequestInterceptor { template ->
            val resourceToken = getResourceToken()
            template.header("Authorization", "Bearer ${resourceToken}")
        }
    }

    @Bean
    fun postErrorDecoder(): ErrorDecoder {
        return CustomErrorDecoder()
    }

    @Bean
    fun postFeignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL // Options: NONE, BASIC, HEADERS, FULL
    }

    private fun getFriendsList(username: String): List<String> {
        val friends = mutableListOf<String>()
        friendsRepo.findByUsername(username).friends.forEach{
            friends.add(it.username)
        }
        logger.info("Adding friends: ${friends.toString()}")
        return friends
    }

    private fun getGroupsList(username: String): List<Long> {
        logger.info("Adding groups...")
        return userRepo.findGroupsIdByUsername(username).toList()
    }

    private fun getResourceToken(): String {
        val claims = HashMap<String, Any?>()
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        logger.warn("Auth username: " + username)
        claims["username"] = username
        if(username == "anonymousUser"){
            claims["friendsList"] = emptyList<String>()
            claims["groupsList"] = emptyList<Long>()
        }
        else{
            logger.warn("Getting user info....")
            claims["friendsList"] = getFriendsList(username)
            claims["groupsList"] = getGroupsList(username)
        }



        val key = Base64.getEncoder().encodeToString(jwtSecret.toByteArray())
        val token = Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS256, key)
            .compact()

        return token
    }

}

@Component
class PostFallbackFactory : FallbackFactory<PostService> {
    override fun create(cause: Throwable?): PostService {
        return object : PostService {
            override fun getPost(id: Long): PostDTO {
                throw cause!!
            }

            override fun getPublicPosts(page: Int, size: Int): PostPaginationDTO {
                throw cause!!
            }

            override fun getUserFeed(page: Int, size: Int): PostPaginationDTO {
                throw cause!!
            }

            override fun getMyPosts(page: Int, size: Int): PostPaginationDTO {
                throw cause!!
            }

            override fun getUserPosts(username: String, page: Int, size: Int): PostPaginationDTO {
                throw cause!!
            }

            override fun getGroupFeed(id: Long, page: Int, size: Int): PostPaginationDTO{
                throw cause!!
            }

            override fun publishPost(newPost: PostDTO): PostDTO {
                throw cause!!
            }

            override fun deletePost(id: Long): Boolean {
                throw cause!!
            }

            override fun updatePost(id: Long, updatedPost: PostDTO): PostDTO {
                throw cause!!
            }

        }
    }
}

