package com.app.pipelines.Data

import jakarta.persistence.*
import org.springframework.data.repository.CrudRepository
import java.time.Instant

enum class Visibility {
    PRIVATE,
    FRIEND_ONLY,
    PUBLIC
}

@Entity
@Table(name = "Pipeline")
data class PipelineDAO(
    @Id
    @GeneratedValue
    val id: Long,
    val name: String,

    val originalImage: Long?,

    @Column(columnDefinition = "TEXT") //so that we can store long strings (json objects)
    val transformations: String,

    @Enumerated(EnumType.STRING)
    var visibility: Visibility,

    val userOwner: String,

    val description : String,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant =  Instant.now()
){
    @PrePersist
    fun onCreate(){
        createdAt= Instant.now()
    }
}

interface PipelineRepository: CrudRepository<PipelineDAO, Long> {
    fun findAllByUserOwner(userOwner: String): List<PipelineDAO>
}
