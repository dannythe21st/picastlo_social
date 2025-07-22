package com.app.socialapp.service

import com.app.socialapp.data.FriendsRepository
import com.app.socialapp.data.ImageDTO
import com.app.socialapp.data.UserRepository
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
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@FeignClient("ImageService", configuration = [ImageServiceConfig::class],fallbackFactory = ImageFallbackFactory::class)
interface ImageService {

    @GetMapping("/images/{id}")
    fun getImage(@PathVariable("id") id: Long): ImageDTO

    @GetMapping("/images/public/{id}")
    fun getPublicImage(@PathVariable("id") id: Long): ImageDTO

    @GetMapping("/images/{username}/album")
    fun getUserImages(@PathVariable("username") username: String): List<ImageDTO>

    @PostMapping("/images/base64", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createImageFromText(@RequestPart("imageBase64") imageBase64: String): ImageDTO

    @PostMapping("/images", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createImage(@RequestPart("file") file: MultipartFile): ImageDTO

    @DeleteMapping("/images/{id}")
    fun deleteImage(@PathVariable("id") id: Long)
}

@Configuration
class ImageServiceConfig(
    @Value("\${jwt.secret}") val jwtSecret: String,
    @Value("\${jwt.expiration}") val expiration: Long,
    @Value("\${jwt.subject}") val subject: String,
    val friendsRepo: FriendsRepository,
    val userRepo: UserRepository
) {

    val logger: org.slf4j.Logger = LoggerFactory.getLogger(ImageServiceConfig::class.java)

    @Bean
    fun imageAPIInterceptor(): RequestInterceptor {
        return RequestInterceptor { template ->
            val resourceToken = getResourceToken()
            template.header("Authorization", "Bearer ${resourceToken}")
        }
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

    private fun getResourceToken():String {
        val claims = HashMap<String, Any?>()

        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        logger.warn("Auth username: "+username)
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

    @Bean
    fun imageErrorDecoder(): ErrorDecoder {
        return CustomErrorDecoder()
    }

    @Bean
    fun imageFeignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL // Options: NONE, BASIC, HEADERS, FULL
    }
}

// Alternative methods include a fallback factory that need to be enabled in the configuration

@Component
class ImageFallbackFactory : FallbackFactory<ImageService> {
    override fun create(cause: Throwable?): ImageService {
        return object : ImageService {
            override fun getImage(id: Long): ImageDTO {
                throw cause!!
            }

            override fun getPublicImage(id: Long): ImageDTO {
                throw cause!!
            }

            override fun getUserImages(username: String): List<ImageDTO> {
                throw cause!!
            }

            override fun createImageFromText(imageBase64: String): ImageDTO {
                throw cause!!
            }

            override fun createImage(file: MultipartFile): ImageDTO {
                throw cause!!
            }

            override fun deleteImage(id: Long) {
                throw cause!!
            }

        }
    }
}
