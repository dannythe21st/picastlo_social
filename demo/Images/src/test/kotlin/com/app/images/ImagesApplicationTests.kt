package com.app.images

import com.app.images.config.filters.JWTUtils
import com.app.images.data.ImageDAO
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import com.app.images.data.ImageDTO
import com.app.images.data.ImageRepository
import com.app.images.data.Visibility
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import java.util.Date
import java.util.Optional
import kotlin.test.assertEquals


@SpringBootTest
@AutoConfigureMockMvc
class ImagesApplicationTests {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var repo: ImageRepository

    @Autowired
    lateinit var jwtUtil: JWTUtils

    companion object {
        val objectMapper = jacksonObjectMapper()

        val content = byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte())

        val image1 = ImageDAO(5, content, "rcosta", Visibility.PUBLIC, 0)
        val image2 = ImageDAO(6, content, "rcosta", Visibility.PUBLIC, 0)
        val image3 = ImageDAO(5, content, "rcosta", Visibility.PRIVATE, 0)

        val imageDto = ImageDTO(5, content, "rcosta", Visibility.PUBLIC, 0)
        val imageDto2 = ImageDTO(5, content, "rcosta", Visibility.PRIVATE, 0)

        val dbImages = listOf(image1, image2)

        val apiImages = dbImages.map { ImageDTO(it.id, it.image, it.userOwner, it.visibility, it.groupId) }
    }

    fun createToken(): String {
        val claims = HashMap<String, Any?>()
        claims["username"] = "rcosta"
        claims["friendsList"] = listOf("rcosta")
        claims["groupsList"] = listOf(1)

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
    fun `Create image`() {
        Mockito.`when`(repo.save(Mockito.any(ImageDAO::class.java))).thenReturn(image1)

        val name = "gold.png"
        val mockFile = MockMultipartFile(
            "file",        // Form field name in the controller
            name,          // Original file name
            "image/png",   // MIME type
            content // Content of the file
        )

        val token = createToken()

        val body = mvc.perform(
            multipart("/images")
                .file(mockFile)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<ImageDTO>() {})

        assertEquals(response.id, imageDto.id)
        assertEquals(response.userOwner, imageDto.userOwner)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get image`() {
        Mockito.`when`(repo.findById(5)).thenReturn(Optional.of(image1))

        val token = createToken()

        val body = mvc.perform(
            get("/images/5")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<ImageDTO>() {})

        assertEquals(response.id, imageDto.id)
        assertEquals(response.userOwner, imageDto.userOwner)
    }

    @Test
    fun `Get public image`() {
        Mockito.`when`(repo.findById(5)).thenReturn(Optional.of(image1))
        val body = mvc.perform(get("/images/public/5"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<ImageDTO>() {})

        assertEquals(response.id, imageDto.id)
        assertEquals(response.userOwner, imageDto.userOwner)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get all images of rcosta`() {
        Mockito.`when`(repo.findAllByUserOwner("rcosta")).thenReturn(dbImages)

        val token = createToken()
        val body = mvc.perform(
            get("/images/rcosta/album")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val images = objectMapper.readValue(body, object : TypeReference<List<ImageDTO>>() {})

        assert(images.isNotEmpty())
        assertEquals(images.size, apiImages.size)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Update image visibility`() {
        Mockito.`when`(repo.findById(5)).thenReturn(Optional.of(image3))
        Mockito.`when`(repo.save(Mockito.any(ImageDAO::class.java))).thenReturn(image3)

        val token = createToken()

        val body = mvc.perform(put("/images/5")
                .param("visibility", "PRIVATE")
                .param("groupid", "0")
                .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<ImageDTO>() {})

        assertEquals(response.id, imageDto2.id)
        assertEquals(response.visibility, imageDto2.visibility)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Delete image`() {
        Mockito.`when`(repo.findById(5)).thenReturn(Optional.of(image1))

        val token = createToken()

        mvc.perform(delete("/images/5")
                .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString
    }
}
