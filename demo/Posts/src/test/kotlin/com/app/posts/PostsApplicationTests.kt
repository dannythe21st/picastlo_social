package com.app.posts

import com.app.posts.config.filters.JWTUtils
import com.app.posts.data.PostDAO
import com.app.posts.data.PostDTO
import com.app.posts.data.PostPaginationDTO
import com.app.posts.data.PostRepository
import com.app.posts.data.Visibility
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import java.util.Date
import java.util.Optional
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class PostsApplicationTests {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var repo: PostRepository

    @Autowired
    lateinit var jwtUtil: JWTUtils

    companion object {
        val objectMapper = jacksonObjectMapper().findAndRegisterModules()

        val post1 = PostDAO(5, "rcosta", 5, 5, "1", 0, Visibility.PUBLIC)
        val post2 = PostDAO(6, "rcosta", 6, 6, "2", 0, Visibility.PUBLIC)

        val postDto = PostDTO(5, "rcosta", 5, 5, 0, "1", Visibility.PUBLIC, null)

        val dbPosts = listOf(post1, post2)

        val apiPosts = PostPaginationDTO(dbPosts.map { PostDTO(it.id, it.username, it.image, it.pipeline, it.groupId, it.text,
            it.visibility, it.createdAt) }, 0)

        const val page = 0
        const val size = 2
    }

    fun createToken(): String {
        val claims = HashMap<String, Any?>()
        claims["username"] = "rcosta"
        claims["friendsList"] = listOf("rcosta")
        claims["groupsList"] = listOf(0)

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(jwtUtil.subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtUtil.expiration))
            .signWith(SignatureAlgorithm.HS256, jwtUtil.key)
            .compact()
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Publish post`() {
        Mockito.`when`(repo.save(Mockito.any(PostDAO::class.java))).thenReturn(post1)

        val token = createToken()
        val body = mvc.perform(multipart("/post")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postDto))
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<PostDTO>() {})

        assertEquals(response.id, postDto.id)
        assertEquals(response.pipeline, postDto.pipeline)
        assertEquals(response.image, postDto.image)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get post`() {
        Mockito.`when`(repo.findById(5)).thenReturn(Optional.of(post1))

        val token = createToken()

        val body = mvc.perform(get("/post/5")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<PostDTO>() {})

        assertEquals(response.id, postDto.id)
        assertEquals(response.pipeline, postDto.pipeline)
        assertEquals(response.image, postDto.image)
    }

    @Test
    fun `Get public post`() {
        Mockito.`when`(repo.findAllByPublic(PageRequest.of(page, size))).thenReturn(dbPosts)

        val body = mvc.perform(get("/post/public")
            .param("page", page.toString())
            .param("size", size.toString()))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<PostPaginationDTO>() {})

        assertEquals(response, apiPosts)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get all posts of rcosta`() {
        Mockito.`when`(repo.findAllByUsername("rcosta", PageRequest.of(page, size))).thenReturn(dbPosts)

        val token = createToken()
        val body = mvc.perform(
            get("/post/personal")
                .header("Authorization", "Bearer $token")
                .param("page", page.toString())
                .param("size", size.toString()))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val images = objectMapper.readValue(body, object : TypeReference<PostPaginationDTO>() {})

        assertEquals(images, apiPosts)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get feed of rcosta`() {
        val token = createToken()

        Mockito.`when`(repo.findAllByFriendsGroupsAndPublic(listOf("rcosta"), listOf(0)
            ,PageRequest.of(page, size))).thenReturn(dbPosts)

        val body = mvc.perform(
            get("/post/feed")
                .header("Authorization", "Bearer $token")
                .param("page", page.toString())
                .param("size", size.toString()))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val images = objectMapper.readValue(body, object : TypeReference<PostPaginationDTO>() {})

        assertEquals(images, apiPosts)
    }

}
