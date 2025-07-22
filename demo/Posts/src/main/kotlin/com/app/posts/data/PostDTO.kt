package com.app.posts.data

import java.time.Instant

data class PostDTO(
    val id : Long,
    val user: String,
    val image: Long,
    val pipeline: Long,
    var groupId: Long,
    val text: String,
    val visibility: Visibility,
    val createdAt: Instant?
)


data class PostPaginationDTO(
    val list: List<PostDTO>,
    val max: Int
)


