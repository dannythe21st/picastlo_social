package com.app.images.data


data class ImageDTO(
    val id: Long,
    val image: ByteArray,
    val userOwner: String,
    val visibility: Visibility,
    val groupId: Long
)

data class ImageOfUserDTO(
    val image: ByteArray,
    val userOwner : String
)





