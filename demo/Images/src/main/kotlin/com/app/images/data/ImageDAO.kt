package com.app.images.data

import jakarta.persistence.*
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.time.Instant
import java.util.*

enum class Visibility {
    PRIVATE,
    GROUP,
    FRIEND_ONLY,
    PUBLIC
}

@Entity
@Table(name = "Image")
data class ImageDAO(
    @Id
    @GeneratedValue
    val id: Long,

    @Lob
    val image: ByteArray,

    val userOwner: String,

    var visibility: Visibility,

    @Column(nullable = false, updatable = false)
    var groupId: Long,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant =  Instant.now()

){

    protected constructor() : this(0, ByteArray(0),"", Visibility.PUBLIC, 0)

    @PrePersist
    fun onCreate(){
        createdAt= Instant.now()
        if (visibility != Visibility.GROUP) {
            groupId = -1
        }
    }
}

interface ImageRepository : CrudRepository<ImageDAO, Long> {
    fun findAllByUserOwner(userOwner:String): List<ImageDAO>
}