package com.app.socialapp.presentation

import com.app.socialapp.application.MainFriendshipServiceApp
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RestController

@RestController
class MainFriendshipServiceController (val app: MainFriendshipServiceApp) : MainFriendshipServiceAPI{

    override fun addFriend(friendUsername: String): Boolean {

        if (friendUsername.isBlank())
            throw InvalidRequest()

        val authentication = SecurityContextHolder.getContext().authentication

        return app.addFriend(authentication.name,friendUsername)

    }

    override fun removeFriend(friendUsername: String): Boolean {
        if (friendUsername.isBlank())
            throw InvalidRequest()

        val authentication = SecurityContextHolder.getContext().authentication

        return app.removeFriend(authentication.name,friendUsername)
    }

    override fun getFriendsList(username : String): List<String> {
        return app.getFriendsList(username).map {
            friend -> friend.username
        }
    }

}