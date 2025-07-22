package com.app.socialapp.presentation

import com.app.socialapp.application.MainGroupServiceApp
import com.app.socialapp.data.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.*


@ResponseStatus(HttpStatus.BAD_REQUEST)
class UserNotAddedToGroupException() : RuntimeException("User not added to group.")

@ResponseStatus(HttpStatus.NOT_FOUND)
class GroupNotFoundException() : RuntimeException("Group not found.")

@RestController
class MainGroupServiceController (val app: MainGroupServiceApp) : MainGroupServiceAPI{
    private val logger: Logger = LoggerFactory.getLogger(MainGroupServiceController::class.java)

    override fun createGroup(group: CreateGroupDTO): GroupDTO {
        if (group.name.isBlank() || group.owner.isBlank())
            throw InvalidRequest()

        val newGroup = app.createGroup(GroupDAO(0, group.name, group.owner))

        return GroupDTO(newGroup.id, group.name, group.owner, newGroup.members)
    }

    override fun getGroup(groupId: Long): GroupDTO {
        if (groupId < 0){
            throw InvalidRequest()
        }

        val group = app.getGroup(groupId)

        if (!group.isPresent) {
            throw GroupNotFoundException()
        }

        val g = group.get()
        return GroupDTO(groupId, g.name, g.owner, g.members)

    }

    override fun getGroupMembers(groupId: Long): List<String> {
        if (groupId < 0){
            throw InvalidRequest()
        }

        val group = app.getGroup(groupId)

        if (!group.isPresent) {
            throw GroupNotFoundException()
        }
        return group.get().members.toList()
    }

    override fun addMember(id: Long, username: String): GroupDTO {
        if (id<=0 || username.isBlank())
            throw InvalidRequest()

        val g : GroupDAO?
        try{
            g = app.addMember(id,username)
        }catch (ex:Exception){
            throw UserNotAddedToGroupException()
        }
        return GroupDTO(id, g.name, g.owner, g.members)
    }

    override fun getMyGroups(page: Int, size: Int): GroupsPaginationDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        val totalGroups = app.repo.countAllGroupsForUser(authentication.name)
        val allGroups = app.findGroupsForUser(authentication.name,page, size)
        val tmp = allGroups.map { group ->
            GroupDTO(
                id = group.id,
                name = group.name,
                owner = group.owner,
                members = group.members,
            )
        }
        return GroupsPaginationDTO(tmp, totalGroups)
    }

}