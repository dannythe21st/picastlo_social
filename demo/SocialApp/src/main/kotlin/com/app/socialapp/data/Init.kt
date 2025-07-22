package com.app.socialapp.data

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class Init(
    val users:UserRepository,
    val friends: FriendsRepository,
    val groups: GroupRepository,
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        val encoder = BCryptPasswordEncoder()

        val user1 = UserDAO("rcosta", encoder.encode("12345"))
        val friendsList1: Set<UserDAO> = setOf(
            UserDAO("deugenio",""),
        )
        val friendshipT1 = FriendsDAO("rcosta",friendsList1)

        val user2 = UserDAO("deugenio", encoder.encode("12345"))
        val friendsList2: Set<UserDAO> = setOf(
            UserDAO("rcosta",""),
        )
        val friendshipT2 = FriendsDAO("deugenio",friendsList2)

        val user3 = UserDAO("fcosta", encoder.encode("12345"))
        val friendsList3: Set<UserDAO> = setOf(
            UserDAO("rcosta",""),
        )
        val friendshipT3 = FriendsDAO("fcosta",friendsList3)

        //mock users
        val user4 = UserDAO("crisRonaldo", encoder.encode("12345"))
        val friendsList4: Set<UserDAO> = setOf(
            UserDAO("crisRonaldo",""),
        )
        val friendshipT4 = FriendsDAO("crisRonaldo",friendsList4)

        val user5 = UserDAO("PablitoAimar", encoder.encode("12345"))
        val friendsList5: Set<UserDAO> = setOf(
            UserDAO("PablitoAimar",""),
        )
        val friendshipT5 = FriendsDAO("PablitoAimar",friendsList5)

        val user6 = UserDAO("IronMike", encoder.encode("12345"))
        val friendsList6: Set<UserDAO> = setOf(
            UserDAO("IronMike",""),
        )
        val friendshipT6 = FriendsDAO("IronMike",friendsList6)

        val user7 = UserDAO("CharlesCoins", encoder.encode("12345"))
        val friendsList7: Set<UserDAO> = setOf(
            UserDAO("CharlesCoins",""),
        )
        val friendshipT7 = FriendsDAO("CharlesCoins",friendsList7)

        val user8 = UserDAO("PabloEscobar", encoder.encode("12345"))
        val friendsList8: Set<UserDAO> = setOf(
            UserDAO("PabloEscobar",""),
        )
        val friendshipT8 = FriendsDAO("PabloEscobar",friendsList8)


        users.saveAll(listOf(user1, user2, user3,user4,user5,user6,user7,user8))
        friends.saveAll(listOf(friendshipT1,friendshipT2,friendshipT3,friendshipT4,
            friendshipT5,friendshipT6,friendshipT7,friendshipT8))

        val group1 = GroupDAO(0,"Grupo 1","rcosta")
        val group2 = GroupDAO(0,"Grupo 2","rcosta")
        val group3 = GroupDAO(0,"Grupo 3","deugenio")
        val group4 = GroupDAO(0,"Grupo 4","rcosta")
        val group5 = GroupDAO(0,"Grupo 5","rcosta")
        val group6 = GroupDAO(0,"Grupo 6","deugenio")
        val group7 = GroupDAO(0,"Grupo 7","rcosta")
        val group8 = GroupDAO(0,"Grupo 8","rcosta")
        val group9 = GroupDAO(0,"Grupo 9","deugenio")


        groups.saveAll(listOf(group1,group2,group3,group4,group5,group6,group7,group8,group9))

    }
}