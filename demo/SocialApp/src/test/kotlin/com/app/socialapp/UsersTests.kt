package com.app.socialapp

import com.app.socialapp.data.CreateGroupDTO
import com.app.socialapp.data.CreateUserDTO
import com.app.socialapp.data.FriendsDAO
import com.app.socialapp.data.FriendsRepository
import com.app.socialapp.data.GroupDAO
import com.app.socialapp.data.GroupDTO
import com.app.socialapp.data.GroupRepository
import com.app.socialapp.data.UserDAO
import com.app.socialapp.data.UserDTO
import com.app.socialapp.data.UserRepository
import com.app.socialapp.data.UsersPaginationDTO
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class UsersTests {

    @Autowired
    private lateinit var mvc:MockMvc

    @MockBean
    private lateinit var repo: UserRepository

    @MockBean
    private lateinit var repoFriend: FriendsRepository

    @MockBean
    private lateinit var repoGroup: GroupRepository

    companion object {
        val objectMapper = jacksonObjectMapper()

        //-------- User Values ------------//
        val dao1 = UserDAO("fd.costa", "12345")
        val dao2 = UserDAO("fcosta", "12345")
        val dbUsers = listOf(dao1, dao2)

        val dto1 = UserDTO("fd.costa")
        val dto2 = UserDTO("fcosta")
        val apiUsers = UsersPaginationDTO(listOf(dto1, dto2), 0)

        //-------- Friends Values ------------//
        val friendsDao2 = FriendsDAO("rcosta", setOf(dao2))

        //-------- Group Values ------------//
        val groupDao1 = GroupDAO(0, "group", "rcosta")
        val groupDao2 = GroupDAO(1, "group", "rcosta")

        val groupDto1 = GroupDTO(0, "group", "rcosta", setOf("rcosta"))
        val groupDto2 = GroupDTO(1, "group", "rcosta", setOf("rcosta", "fcosta"))
    }

    //--------------------------- Users Testing ------------------------//
    @Test
    fun `Create user`() {
        Mockito.`when`(repo.save(Mockito.any(UserDAO::class.java))).thenReturn(dao1)

        val createUserDto1 = CreateUserDTO("fdcosta", "12345")
        val body = mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createUserDto1)))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<UserDTO>() {})

        assertEquals(response, UserDTO("fd.costa"))
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get user`() {
        Mockito.`when`(repo.findById("fcosta")).thenReturn(Optional.of(dao2))

        val body = mvc.perform(get("/users/fcosta"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<UserDTO>() {})

        assertEquals(response, dto2)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get all users`() {
        val pageable: Pageable = PageRequest.of(0, 10)
        Mockito.`when`(repo.findAllUsers(pageable)).thenReturn(dbUsers)

        val body = mvc.perform(get("/users")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<UsersPaginationDTO>() {})

        assertEquals(response, apiUsers)
    }

    //--------------------------- Friends Testing ------------------------//
    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Add friend`() {
        val friendsDao1 = FriendsDAO()
        Mockito.`when`(repoFriend.findById("rcosta")).thenReturn(Optional.of(friendsDao1))
        Mockito.`when`(repo.findById("fcosta")).thenReturn(Optional.of(dao2))

        val body = mvc.perform(post("/friends/fcosta"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<Boolean>() {})

        assert(response)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Remove friend`() {
        Mockito.`when`(repoFriend.findById("rcosta")).thenReturn(Optional.of(friendsDao2))
        Mockito.`when`(repo.findById("fcosta")).thenReturn(Optional.of(dao2))

        val body = mvc.perform(delete("/friends/fcosta"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<Boolean>() {})

        assert(response)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get friends`() {
        Mockito.`when`(repoFriend.findByUsername("rcosta")).thenReturn(friendsDao2)

        val body = mvc.perform(get("/friends"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<List<String>>() {})

        assertEquals(response, listOf("fcosta"))
    }

    //--------------------------- Group Testing ------------------------//
    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Create group`() {
        val createGroupDto1 = CreateGroupDTO("group", "rcosta")
        Mockito.`when`(repoGroup.save(groupDao1)).thenReturn(groupDao1)

        val body = mvc.perform(post("/groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createGroupDto1)))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<GroupDTO>() {})

        assertEquals(response, groupDto1)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get group`() {
        Mockito.`when`(repoGroup.findById(0)).thenReturn(Optional.of(groupDao1))

        val body = mvc.perform(get("/groups/0"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<GroupDTO>() {})

        assertEquals(response, groupDto1)
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Get group members`() {
        Mockito.`when`(repoGroup.findById(0)).thenReturn(Optional.of(groupDao1))

        val body = mvc.perform(get("/groups/0/members"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<List<String>>() {})

        assertEquals(response, groupDto1.members.toList())
    }

    @Test
    @WithMockUser(username = "rcosta", password = "12345")
    fun `Add member`() {
        Mockito.`when`(repoGroup.findById(1)).thenReturn(Optional.of(groupDao2))

        Mockito.`when`(repo.findById("fcosta")).thenReturn(Optional.of(dao2))

        val updatedUser = UserDAO("fcosta", "12345", setOf(0))
        val updatedGroup = GroupDAO(1, "group", "rcosta", setOf("rcosta", "fcosta"))

        Mockito.`when`(repo.save(updatedUser)).thenReturn(updatedUser)
        Mockito.`when`(repoGroup.save(updatedGroup)).thenReturn(updatedGroup)

        val body = mvc.perform(put("/groups/1/fcosta"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assert(body.isNotEmpty())

        val response = objectMapper.readValue(body, object: TypeReference<GroupDTO>() {})

        assertEquals(response, groupDto2)
    }
}