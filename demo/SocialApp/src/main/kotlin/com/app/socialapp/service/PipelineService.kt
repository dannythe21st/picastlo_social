package com.app.socialapp.service

import com.app.socialapp.data.FriendsRepository
import com.app.socialapp.data.PipelineDTO
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
import java.util.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@FeignClient("PipelineService", configuration = [PipelineServiceConfig::class],fallbackFactory = PipelineFallbackFactory::class)
interface PipelineService {

    @GetMapping("/pipeline/{id}")
    fun getPipeline(@PathVariable("id") id: Long): PipelineDTO

    @GetMapping("/pipeline/public/{id}")
    fun getPublicPipeline(@PathVariable("id") id: Long): PipelineDTO

    @GetMapping("/pipeline/all/{username}")
    fun getUserPipelines(@PathVariable("username") username: String): List<PipelineDTO>

    @PostMapping("/pipeline", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPipeline(@RequestParam("description") description: String,
                       @RequestParam("name") name: String,
                       @RequestParam("id") id: Long,
                       @RequestPart("transformations") transformations: String,
    ): PipelineDTO

    /**
     * the new visibility can either be 0,1 or 2.
     * These match to private, friend-only or public, respectively
     */
    @PutMapping("/pipeline/{id}")
    fun changePipelineVisibility(@PathVariable("id") id: Long, newVis : Int): PipelineDTO
}


@Configuration
class PipelineServiceConfig(
    @Value("\${jwt.secret}") val jwtSecret: String,
    @Value("\${jwt.expiration}") val expiration: Long,
    @Value("\${jwt.subject}") val subject: String,
    val friendsRepo: FriendsRepository,
    val userRepo: UserRepository
) {

    val logger: org.slf4j.Logger = LoggerFactory.getLogger(PipelineServiceConfig::class.java)

    @Bean
    fun pipelineAPIInterceptor(): RequestInterceptor {
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
    fun errorDecoder(): ErrorDecoder {
        return CustomErrorDecoder()
    }

    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL // Options: NONE, BASIC, HEADERS, FULL
    }
}

@Component
class PipelineFallbackFactory : FallbackFactory<PipelineService> {
    override fun create(cause: Throwable?): PipelineService {
        return object : PipelineService {
            override fun getPipeline(id: Long): PipelineDTO {
                throw cause!!
            }

            override fun getPublicPipeline(id: Long): PipelineDTO {
                throw cause!!
            }

            override fun getUserPipelines(email: String): List<PipelineDTO> {
                throw cause!!
            }

            override fun createPipeline(
                description: String,
                name: String,
                id: Long,
                transformations: String,
            ): PipelineDTO {
                throw cause!!
            }

            override fun changePipelineVisibility(id: Long, newVis: Int) : PipelineDTO {
                throw cause!!
            }

        }
    }
}
