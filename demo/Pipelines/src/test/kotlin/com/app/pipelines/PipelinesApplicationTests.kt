package com.app.pipelines

import com.app.pipelines.Data.PipelineDAO
import com.app.pipelines.Data.PipelineRepository
import com.app.pipelines.Data.Visibility
import com.app.pipelines.config.filters.JWTUtils
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import com.app.pipelines.presentation.PipelineDTO
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
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import java.util.Date
import java.util.Optional
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class PipelinesApplicationTests {

	@Autowired
	private lateinit var mvc: MockMvc

	@MockBean
	private lateinit var repo: PipelineRepository

	@Autowired
	lateinit var jwtUtil: JWTUtils

	companion object {
		val objectMapper = jacksonObjectMapper().findAndRegisterModules()

		val content = byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte())

		val pipeline1 = PipelineDAO(5, "5", 5, "transformations", Visibility.PUBLIC, "rcosta", "desc")
		val pipeline2 = PipelineDAO(6, "6", 6, "transformations", Visibility.PUBLIC, "rcosta", "desc")
		val pipeline3 = PipelineDAO(5, "5", 5, "transformations", Visibility.PRIVATE, "rcosta", "desc")

		val pipelineDto = PipelineDTO(5, "5", 5, "transformations", Visibility.PUBLIC, "rcosta", "desc", null)
		val pipelineDto2 = PipelineDTO(5, "5", 5, "transformations", Visibility.PRIVATE, "rcosta", "desc", null)

		val dbPipelines = listOf(pipeline1, pipeline2)

		val apiPipelines = dbPipelines.map { PipelineDTO(it.id, it.name, it.originalImage, it.transformations,
			it.visibility, it.userOwner, it.description, it.createdAt) }
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
	fun `Create pipeline`() {
		Mockito.`when`(repo.save(Mockito.any(PipelineDAO::class.java))).thenReturn(pipeline1)

		val name = "gold.png"
		val mockFile = MockMultipartFile(
			"file",        // Form field name in the controller
			name,          // Original file name
			"image/png",   // MIME type
			content // Content of the file
		)

		val desc = "a"
		val trans = "transformations"
		val transformations = MockMultipartFile(trans, trans, "text/plain", trans.toByteArray());

		val token = createToken()
		val body = mvc.perform(multipart("/pipeline")
			.file(mockFile)
			.file(transformations)
			.param("description", desc)
			.param("name", name)
			.param("id", "0")
			.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
			.header("Authorization", "Bearer $token"))
			.andExpect(status().isOk)
			.andReturn()
			.response
			.contentAsString

		assert(body.isNotEmpty())

		val response = objectMapper.readValue(body, object : TypeReference<PipelineDTO>() {})

		assertEquals(response.id, pipelineDto.id)
		assertEquals(response.name, pipelineDto.name)
		assertEquals(response.originalImage, pipelineDto.originalImage)
	}

	@Test
	@WithMockUser(username = "rcosta", password = "12345")
	fun `Get pipeline`() {
		Mockito.`when`(repo.findById(5)).thenReturn(Optional.of(pipeline1))

		val token = createToken()

		val body = mvc.perform(
			get("/pipeline/5")
				.header("Authorization", "Bearer $token")
		)
			.andExpect(status().isOk)
			.andReturn()
			.response
			.contentAsString

		assert(body.isNotEmpty())

		val response = objectMapper.readValue(body, object : TypeReference<PipelineDTO>() {})

		assertEquals(response.id, pipelineDto.id)
		assertEquals(response.name, pipelineDto.name)
		assertEquals(response.originalImage, pipelineDto.originalImage)
	}

	@Test
	fun `Get public pipeline`() {
		Mockito.`when`(repo.findById(5)).thenReturn(Optional.of(pipeline1))
		val body = mvc.perform(get("/pipeline/public/5"))
			.andExpect(status().isOk)
			.andReturn()
			.response
			.contentAsString

		assert(body.isNotEmpty())

		val response = objectMapper.readValue(body, object : TypeReference<PipelineDTO>() {})

		assertEquals(response.id, pipelineDto.id)
		assertEquals(response.name, pipelineDto.name)
		assertEquals(response.originalImage, pipelineDto.originalImage)
	}

	@Test
	@WithMockUser(username = "rcosta", password = "12345")
	fun `Get all pipelines of rcosta`() {
		Mockito.`when`(repo.findAllByUserOwner("rcosta")).thenReturn(dbPipelines)

		val token = createToken()
		val body = mvc.perform(
			get("/pipeline/all/rcosta")
				.header("Authorization", "Bearer $token")
		)
			.andExpect(status().isOk)
			.andReturn()
			.response
			.contentAsString

		val images = objectMapper.readValue(body, object : TypeReference<List<PipelineDTO>>() {})

		assert(images.isNotEmpty())
		assertEquals(images, apiPipelines)
	}

	@Test
	@WithMockUser(username = "rcosta", password = "12345")
	fun `Update  pipeline visibility`() {
		Mockito.`when`(repo.findById(5)).thenReturn(Optional.of(pipeline3))
		Mockito.`when`(repo.save(Mockito.any(PipelineDAO::class.java))).thenReturn(pipeline3)

		val token = createToken()
		val body = mvc.perform(put("/pipeline/5")
			.param("vis", "0")
			.header("Authorization", "Bearer $token"))
			.andExpect(status().isOk)
			.andReturn()
			.response
			.contentAsString

		assert(body.isNotEmpty())

		val response = objectMapper.readValue(body, object : TypeReference<PipelineDTO>() {})

		assertEquals(response.id, pipelineDto2.id)
		assertEquals(response.visibility, pipelineDto2.visibility)
	}

}
