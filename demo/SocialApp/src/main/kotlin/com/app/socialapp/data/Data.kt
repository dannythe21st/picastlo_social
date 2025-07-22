package com.app.socialapp.data

import jakarta.persistence.*
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Pageable

@Entity
@Schema(description = "Entity representing a user in the database with sensitive information.")
data class UserDAO(

    @Id
    @Schema(description = "Unique username of the user, used as the primary key.", example = "johndoe")
    val username: String,

    @Column(nullable = false)
    @Schema(description = "Password of the user, stored in a hashed format.", example = "hashedPassword123")
    val password: String,

    @ElementCollection
    @CollectionTable(name="user_groups", joinColumns = [JoinColumn(name="user_ids")])
    @Column(name="groups_ids")
    @Schema(description = "IDs of the groups that the user is a part of")
    val groupsId: Set<Long> = emptySet()
) {
    constructor() : this("", "")
}

@Entity
@Schema(description = "Entity representing a friendship")
data class FriendsDAO(
                    @Id
                    @Schema(description = "Unique username of the user, used as the primary key.", example = "johndoe")
                    val username: String,

                    @OneToMany @JoinColumn(name = "owner_id")
                    @Schema(description = "Friends List")
                    val friends: Set<UserDAO> = HashSet()) {
    constructor() : this("")
}

@Entity
@Schema(description = "Entity representing a group with a unique identifier, name, owner, and members.")
data class GroupDAO(

    @Id
    @GeneratedValue
    @Schema(description = "Unique identifier for the group.", example = "1")
    val id: Long,

    @Column(nullable = false)
    @Schema(description = "Name of the group.", example = "Developers Group")
    val name: String,

    @Column(nullable = false)
    @Schema(description = "Username of the group owner.", example = "johndoe")
    val owner: String,

    @ElementCollection
    @CollectionTable(name="user_groups", joinColumns = [JoinColumn(name="groups_ids")])
    @Column(name="user_ids")
    @Schema(description = "Set of users who are members of the group.")
    var members: Set<String> = setOf(owner)

) {
    constructor() : this(0, "", "")
    @Entity
    @Schema(description = "Entity representing a group with a unique identifier, name, owner, and members.")
    data class GroupDAO(

        @Id
        @GeneratedValue
        @Schema(description = "Unique identifier for the group.", example = "1")
        val id: Long,

        @Column(nullable = false)
        @Schema(description = "Name of the group.", example = "Developers Group")
        val name: String,

        @Column(nullable = false)
        @Schema(description = "Username of the group owner.", example = "johndoe")
        val owner: String,

        @ElementCollection
        @CollectionTable(name="user_groups", joinColumns = [JoinColumn(name="groups_ids")])
        @Column(name="user_ids")
        @Schema(description = "Set of users who are members of the group.")
        var members: Set<String> = setOf(owner)

    ) {
        constructor() : this(0, "", "")
    }
}
interface UserRepository : CrudRepository<UserDAO, String>{

    @Query("SELECT u.groupsId FROM UserDAO u WHERE u.username = :username")
    fun findGroupsIdByUsername(username: String): Set<Long>

    @Query("SELECT COUNT(*) FROM UserDAO u")
    fun countAllUsers(): Int

    @Query("SELECT u FROM UserDAO u")
    fun findAllUsers(pageable: Pageable): List<UserDAO>
}

interface GroupRepository : CrudRepository<GroupDAO, Long>{

    @Query("SELECT COUNT(*) FROM GroupDAO g WHERE :username MEMBER OF g.members")
    fun countAllGroupsForUser(username: String): Int

    @Query("SELECT g FROM GroupDAO g WHERE :username MEMBER OF g.members")
    fun findAllGroupsForUser(username: String, pageable: Pageable): List<GroupDAO>
}

interface FriendsRepository : CrudRepository<FriendsDAO, String>{
    fun findByUsername(username: String): FriendsDAO
}
