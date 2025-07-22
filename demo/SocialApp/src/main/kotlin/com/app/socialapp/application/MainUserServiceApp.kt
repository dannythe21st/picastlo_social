package com.app.socialapp.application

import com.app.socialapp.data.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
//import org.springframework.transaction.annotation.Transactional
import java.util.Optional


@Component
class MainUserServiceApp(val repo: UserRepository,val repoFriend: FriendsRepository) {

    fun createUser(newUser : UserDAO) : UserDAO{
        repoFriend.save(FriendsDAO("rcosta"))
        return repo.save(newUser)
    }

    fun updateUser(updatedUser: UserDAO): UserDAO{
        return repo.save(updatedUser)
    }

    fun deleteUser(deletedUser: UserDAO): String{
        repoFriend.deleteById(deletedUser.username)
        repo.delete(deletedUser)
        return deletedUser.username
    }

    fun getAllUsers(page: Int, size: Int): List<UserDAO>{
        val pageable: Pageable = PageRequest.of(page, size)
        return repo.findAllUsers(pageable)
    }

    fun findUser(username : String): Optional<UserDAO> {
        return repo.findById(username)
    }
}