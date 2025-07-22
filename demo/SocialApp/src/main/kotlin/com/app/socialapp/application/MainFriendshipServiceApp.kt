package com.app.socialapp.application

import com.app.socialapp.data.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*

@ResponseStatus(HttpStatus.BAD_REQUEST)
class UsersNotFriendsException() : RuntimeException("These users are not friends")

@ResponseStatus(HttpStatus.CONFLICT)
class UsersAlreadyFriendsException() : RuntimeException("These users are already friends.")

@Component
class MainFriendshipServiceApp(val repo: FriendsRepository, val userRepo: UserRepository) {

    fun addFriend(username: String, newFriend: String) : Boolean {

        val friendship = repo.findById(username)

        val newFriendDAO = userRepo.findById(newFriend).get()

        if (friendship.get().friends.contains(newFriendDAO))
            throw UsersAlreadyFriendsException()

        val f = friendship.get()

        val updatedFriendsList: Set<UserDAO> = f.friends + setOf(newFriendDAO)

        val finalFriendsList = f.copy(friends = updatedFriendsList)

        repo.save(finalFriendsList)

        return true
    }

    fun removeFriend(username: String, friend: String) : Boolean{
        val friendship = repo.findById(username)

        val friendDAO = userRepo.findById(friend).get()

        if (!friendship.get().friends.contains(friendDAO))
            throw UsersNotFriendsException()

        val f = friendship.get()

        val updatedFriendsList: Set<UserDAO> = f.friends - setOf(friendDAO)

        val finalFriendsList = f.copy(friends = updatedFriendsList)

        repo.save(finalFriendsList)

        return true
    }

    fun getFriendsList(username:String):List<UserDAO> {
        return repo.findByUsername(username).friends.toList()
    }
}