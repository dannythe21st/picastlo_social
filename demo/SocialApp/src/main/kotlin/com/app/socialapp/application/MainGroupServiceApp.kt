package com.app.socialapp.application

import com.app.socialapp.data.GroupDAO
import com.app.socialapp.data.GroupRepository
import com.app.socialapp.data.GroupsPaginationDTO
import com.app.socialapp.data.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException() : RuntimeException("User not found")

@Component
class MainGroupServiceApp(val repo: GroupRepository, val userRepo: UserRepository) {

    fun createGroup(newGroup: GroupDAO) :  GroupDAO{
        return repo.save(newGroup)
    }

    fun getGroup(groupID : Long) : Optional<GroupDAO> {
        return repo.findById(groupID)
    }

    fun getGroupMembers(groupID: Long): Set<String>{
        return repo.findById(groupID).get().members
    }

    fun addMember(groupID: Long, newMemberID: String): GroupDAO {
        val group = repo.findById(groupID)

        if (!group.isPresent)
            throw UserNotFoundException()

        val g = group.get()
        val updatedMembers = g.members + newMemberID

        val updatedGroup = g.copy(members = updatedMembers)

        val user = userRepo.findById(newMemberID)
        if (user.isPresent) {
            val u = user.get()
            val updatedGroups = u.groupsId + groupID
            val updatedUser = u.copy(groupsId = updatedGroups)
            userRepo.save(updatedUser)
        } else {
            throw UserNotFoundException()
        }
        return repo.save(updatedGroup)
    }

    fun findGroupsForUser(username: String, page: Int, size: Int) : List<GroupDAO> {
        val pageable: Pageable = PageRequest.of(page, size)
        return repo.findAllGroupsForUser(username, pageable)
    }

}