package com.app.socialapp

import com.app.socialapp.application.MainImageServiceApp
import com.app.socialapp.application.MainPipelineServiceApp
import com.app.socialapp.application.MainPostServiceApp
import com.app.socialapp.data.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.collections.isNotEmpty
import kotlin.test.assertEquals


@SpringBootTest
@AutoConfigureMockMvc
class SocialAppApplicationTests {

    @Autowired
    private lateinit var mvc:MockMvc

    @MockBean
    lateinit var imageApp: MainImageServiceApp

    @MockBean
    lateinit var pipelineApp: MainPipelineServiceApp

    @MockBean
    lateinit var postApp: MainPostServiceApp

    companion object {
        val objectMapper = jacksonObjectMapper()

        //-------- Image Values ------------//
        val content = byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte())
        val image1 = ImageDTO(0, content, "rcosta", Visibility.PRIVATE, 0)
        val image2 = ImageDTO(0, content, "rcosta", Visibility.PRIVATE, 0)
        val apiImages = listOf(image1, image2)

        //-------- Pipeline Values ------------//
        val pipeline1 = PipelineDTO(0, "image1", 5, "transformations", Visibility.PRIVATE, "rcosta", "first image")
        val pipeline2 = PipelineDTO(0, "image2", 6, "transformations", Visibility.PRIVATE, "rcosta", "second image")
        val apiPipelines = listOf(pipeline1, pipeline2)

        //-------- Post Values ------------//
        val post1 = PostDTO(0,"rcosta", 5, 5, "first post", PostVisibility.PUBLIC ,null)
        val post2 = PostDTO(0,"rcosta", 6, 6, "second post", PostVisibility.PUBLIC ,null)
        val apiPosts = PostPaginationDTO(listOf(post1, post2), 2)
    }

    //--------------------------- Image Testing ------------------------//
    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Create image`() {
        val mockFile = MockMultipartFile(
            "file",        // Form field name in the controller
            "gold.png",          // Original file name
            "image/png",   // MIME type
            content // Content of the file
        )

        Mockito.`when`(imageApp.createImage(mockFile)).thenReturn(image1)

        val body = mvc.perform(multipart("/images")
            .file(mockFile)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<ImageDTO>() {})

        assertEquals(response.id, image1.id)
        assertEquals(response.userOwner, image1.userOwner)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get image`() {
        Mockito.`when`(imageApp.getImage(1)).thenReturn(image1)

        val body = mvc.perform(get("/images/1"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<ImageDTO>() {})

        assertEquals(response.id, image1.id)
        assertEquals(response.userOwner, image1.userOwner)
    }

    @Test
    fun `Get public image`() {
        Mockito.`when`(imageApp.getPublicImage(5)).thenReturn(image1)

        val body = mvc.perform(get("/images/public/5"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<ImageDTO>() {})

        assertEquals(response.id, image1.id)
        assertEquals(response.userOwner, image1.userOwner)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get all images of rcosta`() {
        Mockito.`when`(imageApp.getUserImage("rcosta")).thenReturn(apiImages)

        val body = mvc.perform(get("/images/user/rcosta"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val images = objectMapper.readValue(body, object : TypeReference<List<ImageDTO>>() {})

        assert(images.isNotEmpty())
        assertEquals(images.size, apiImages.size)
    }


    //--------------------------- Pipeline Testing ------------------------//
    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Create pipeline`() {
        val name =  "gold.png"

        val desc = "a"
        val trans = "transformations"
        val transformations = MockMultipartFile(trans, trans, "text/plain", trans.toByteArray());
        Mockito.`when`(pipelineApp.createPipeline(desc, name, 1, trans)).thenReturn(pipeline1)

        val body = mvc.perform(multipart("/pipeline")
            .file(transformations)
            .param("description", desc)
            .param("name", name)
            .param("id", "1")
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<PipelineDTO>() {})

        assertEquals(response, pipeline1)
    }
    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get pipeline`() {
        Mockito.`when`(pipelineApp.getPipeline(1)).thenReturn(pipeline1)

        val body = mvc.perform(get("/pipeline/1"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<PipelineDTO>() {})

        assertEquals(response, pipeline1)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get all pipelines of rcosta`() {
        Mockito.`when`(pipelineApp.getUserPipelines("rcosta")).thenReturn(apiPipelines)

        val body = mvc.perform(get("/pipeline/all/rcosta"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val pipelines = objectMapper.readValue(body, object : TypeReference<List<PipelineDTO>>() {})

        assert(pipelines.isNotEmpty())
        assertEquals(pipelines, apiPipelines)
    }

    @Test
    fun `Get public pipeline`() {
        Mockito.`when`(pipelineApp.getPublicPipeline(5)).thenReturn(pipeline1)

        val body = mvc.perform(get("/pipeline/public/5"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<PipelineDTO>() {})

        assertEquals(response, pipeline1)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Change pipeline visibility`() {
        val update = pipeline1
        update.visibility = Visibility.PRIVATE

        Mockito.`when`(pipelineApp.changePipelineVisibility(5, 0)).thenReturn(update)

        val body = mvc.perform(put("/pipeline/5")
            .param("newVis", "0"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<PipelineDTO>() {})

        assertEquals(response, update)
    }

    //--------------------------- Post Testing ------------------------//
    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Publish post`() {

        Mockito.`when`(postApp.publishPost(post1)).thenReturn(post1)

        val body = mvc.perform(post("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(post1)))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<PostDTO>() {})

        assertEquals(response, post1)
    }
    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get post`() {
        Mockito.`when`(postApp.getPost(1)).thenReturn(post1)

        val body = mvc.perform(get("/posts/1"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<PostDTO>() {})

        assertEquals(response, post1)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get all posts of rcosta`() {
        Mockito.`when`(postApp.getUserFeed(1, 1)).thenReturn(apiPosts)

        val body = mvc.perform(get("/posts/feed")
            .param("page" , "1")
            .param("size", "1"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val posts = objectMapper.readValue(body, object : TypeReference<PostPaginationDTO>() {})
        assert(posts.list.isNotEmpty())

        assertEquals(posts, apiPosts)
    }

    @Test
    fun `Get public posts`() {
        Mockito.`when`(postApp.getPublicPosts(2, 2)).thenReturn(apiPosts)

        val body = mvc.perform(get("/posts/public")
            .param("page" , "2")
            .param("size", "2"))
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
    fun `Get own posts`() {
        Mockito.`when`(postApp.getMyPosts(2, 2)).thenReturn(apiPosts)

        val body = mvc.perform(get("/posts/personal")
            .param("page" , "2")
            .param("size", "2"))
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
    fun `Update post`() {
        Mockito.`when`(postApp.updatePost(5, post2)).thenReturn(post2)

        val body = mvc.perform(put("/posts/5")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(post2)))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<PostDTO>() {})

        assertEquals(response, post2)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Delete post`() {
        Mockito.`when`(postApp.deletePost(5)).thenReturn(true)

        val body = mvc.perform(delete("/posts/5"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object : TypeReference<Boolean>() {})

        assert(response)
    }


}
