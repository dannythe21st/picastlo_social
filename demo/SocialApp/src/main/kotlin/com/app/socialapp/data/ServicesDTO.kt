package com.app.socialapp.data

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant


/** PIPELINE DATA **/

enum class Visibility {
    PRIVATE,
    GROUP,
    FRIEND_ONLY,
    PUBLIC
}

@Schema(description = "DTO representing a pipeline with its properties and metadata.")
data class PipelineDTO(
    @Schema(description = "Unique identifier of the pipeline", example = "1")
    val id: Long,
    @Schema(description = "Name of the pipeline", example = "Image Transformation Pipeline")
    val name: String,
    @Schema(description = "Original image data in byte array format", nullable = true)
    val originalImage: Long,
    @Schema(description = "String representation of transformations applied to the image", example = "resize,crop")
    val transformations: String,
    @Schema(description = "Visibility of the pipeline", example = "PUBLIC")
    var visibility: Visibility,
    @Schema(description = "Username or ID of the pipeline owner", example = "johndoe123")
    val owner: String,
    @Schema(description = "Description of the pipeline", example = "This pipeline applies image transformations for resizing and cropping.")
    val description: String
)

/** USER DATA **/


@Schema(description = "DTO representing a user.")
data class UserDTO(

    @Schema(description = "Unique username of the user", example = "johndoe")
    val username: String
)

@Schema(description = "DTO representing user credentials")
data class LoginUserDTO(

    @Schema(description = "Unique username of the user", example = "johndoe")
    val username: String,
    @Schema(description = "Users password", example = "12345")
    val password: String
)

@Schema(description = "DTO representing a user to be created.")
data class CreateUserDTO(

    @Schema(description = "Unique username of the user", example = "johndoe")
    val username: String,
    @Schema(description = "user password", example = "123")
    val password: String
)

/*** GROUP DATA ***/

@Schema(description = "DTO representing an group to be created.")
data class CreateGroupDTO(

    @Schema(description = "Group name", example = "Os 3 Duques")
    val name: String,

    @Schema(description = "Username of the group owner", example = "rcosta")
    val owner: String,
)

@Schema(description = "DTO representing a group.")
data class GroupDTO(

    @Schema(description = "Unique identifier of the group", example = "101")
    val id: Long,

    @Schema(description = "Group name", example = "Os 3 Duques")
    val name: String,

    @Schema(description = "Username of the group owner", example = "rcosta")
    val owner: String,

    @Schema(description = "Set of users who are members of the group.")
    val members: Set<String>
)

/*** IMAGES DATA ***/

@Schema(description = "DTO representing an image and its associated metadata.")
data class ImageDTO(

    @Schema(description = "Unique identifier of the image", example = "101")
    val id: Int,

    @Schema(description = "Image data in byte array format")
    val image: ByteArray,

    @Schema(description = "Username or ID of the image owner", example = "rcosta")
    val userOwner: String,

    @Schema(description = "Visibility of the image", example = "PUBLIC")
    val visibility: Visibility,

    @Schema(description = "ID of the group to which the image was posted", example = "1")
    val groupId: Long
)

/*** POST DATA ***/
enum class PostVisibility {
    GROUP,
    FRIEND_ONLY,
    PUBLIC
}

@Schema(description = "DTO representing a post and its associated metadata.")
data class PostDTO(

    @Schema(description = "ID of the post", example = "28021904")
    val id: Long,

    @Schema(description = "Username of the user that published the post", example = "rcosta349")
    val user: String,

    @Schema(description = "ID of the image in the post", example = "101")
    val image: Long,

    @Schema(description = "ID of the pipeline in the post", example = "101")
    val pipeline: Long,

    @Schema(description = "ID of the group where the post is being posted on", example = "10")
    val groupId: Long,

    @Schema(description = "Caption added by the user", example = "My first post on Picastlo!")
    val text: String,

    @Schema(description = "Visibility of the post", example = "PUBLIC")
    val visibility: PostVisibility,

    @Schema(description = "Post creation Timestamp (in epoch-seconds)", example = "190,402,280")
    val createdAt: Instant?
)

@Schema(description = "DTO representing a page of Posts")
data class PostPaginationDTO(
    @Schema(description = "List of PostDTOs")
    val list : List<PostDTO>,
    @Schema(description = "max elements", example = "10")
    val max : Int
)

@Schema(description = "DTO representing a page of Users")
data class UsersPaginationDTO(
    @Schema(description = "List of UserDTOs")
    val list : List<UserDTO>,
    @Schema(description = "max elements", example = "10")
    val max : Int
)

@Schema(description = "DTO representing a page of Group")
data class GroupsPaginationDTO(
    @Schema(description = "List of GroupDTOs")
    val list : List<GroupDTO>,
    @Schema(description = "max elements", example = "10")
    val max : Int
)




