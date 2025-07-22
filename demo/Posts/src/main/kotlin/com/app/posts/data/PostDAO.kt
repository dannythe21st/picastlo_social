package com.app.posts.data

import jakarta.persistence.*
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import java.time.Instant
import org.springframework.data.jpa.repository.Query


enum class Visibility {
    GROUP,
    FRIEND_ONLY,
    PUBLIC
}

@Entity
@Table(name = "Post")
data class PostDAO(
    @Id
    @GeneratedValue
    val id: Long,
    val username: String,
    val image: Long,
    val pipeline: Long,
    val text: String,

    @Column(nullable = false, updatable = false)
    var groupId: Long,

    @Enumerated(EnumType.STRING)
    var visibility: Visibility,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant =  Instant.now()

){
    protected constructor() : this(0, "", 0,0, "",-1,Visibility.PUBLIC)

    @PrePersist
    fun onCreate(){
        createdAt= Instant.now()
        if (visibility == Visibility.FRIEND_ONLY) { // we can have a public post thats also posted on a group (we think)
            groupId = -1
        }
    }
}

interface PostRepository : CrudRepository<PostDAO, Long> {
    fun findAllByUsername(username:String,pageable: Pageable): List<PostDAO>

    @Query("SELECT p FROM PostDAO p WHERE" +
            " (p.username IN :friends AND (p.visibility = 'FRIEND_ONLY' OR p.visibility = 'PUBLIC')) " +
            "OR (p.groupId IN :groups AND p.visibility = 'GROUP') " +
            "OR p.visibility = 'PUBLIC'")
    fun findAllByFriendsGroupsAndPublic(friends: List<String>, groups: List<Long>,pageable: Pageable): List<PostDAO>

    @Query("SELECT p FROM PostDAO p WHERE p.visibility = 'PUBLIC'")
    fun findAllByPublic(pageable: Pageable): List<PostDAO>

    @Query("SELECT COUNT(*) FROM PostDAO p WHERE p.visibility = 'PUBLIC'")
    fun countAllByPublic(): Int

    @Query("SELECT COUNT(*) FROM PostDAO p WHERE" +
            " (p.username IN :friends AND (p.visibility = 'FRIEND_ONLY' OR p.visibility = 'PUBLIC')) " +
            "OR (p.groupId IN :groups AND p.visibility = 'GROUP') " +
            "OR p.visibility = 'PUBLIC'")
    fun countAllFeedPosts(friends: List<String>, groups: List<Long>): Int

    fun countAllByUsername(username : String): Int

    @Query("SELECT p FROM PostDAO p WHERE p.groupId = :id")
    fun findAllByGroup(id: Long, pageable: Pageable): List<PostDAO>

    @Query("SELECT COUNT(*) FROM PostDAO p WHERE p.groupId = :id")
    fun countAllByGroup(id: Long): Int

}