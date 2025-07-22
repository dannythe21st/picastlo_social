package com.app.pipelines.presentation

import com.app.pipelines.application.PipelineApplication
import com.app.pipelines.Data.PipelineDAO
import com.app.pipelines.Data.Visibility

import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@ResponseStatus(HttpStatus.NOT_FOUND)
class PipelineNotFoundException() : RuntimeException("Pipeline not found")

@ResponseStatus(HttpStatus.FORBIDDEN)
class ForbiddenOperationException() : RuntimeException("Forbidden operation check your permissitions")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class PipelineVisibilityDoesNotExist() : RuntimeException("Inserted visibility does not exist")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class EmptyPipelineException() : RuntimeException("A pipeline must have at least one transformation")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidRequestException() : RuntimeException("Invalid Request - insert valid username")

@RestController
class PipelineController(val app: PipelineApplication): PipelineAPI {
    //private val logger: Logger = LoggerFactory.getLogger(PipelineController::class.java)

    override fun getPipeline(id: Long): PipelineDTO {
        val pipeline = app.getPipeline(id)

        if (!pipeline.isPresent)
            throw PipelineNotFoundException()

        val pipelineTmp = pipeline.get()
        return PipelineDTO(
            pipelineTmp.id,
            pipelineTmp.name,
            pipelineTmp.originalImage,
            pipelineTmp.transformations,
            pipelineTmp.visibility,
            pipelineTmp.userOwner,
            pipelineTmp.description,
            pipelineTmp.createdAt
        )
    }

    override fun getPublicPipeline(id: Long): PipelineDTO {
        val pipeline = app.getPipeline(id)

        if (!pipeline.isPresent)
            throw PipelineNotFoundException()

        val pipelineTmp = pipeline.get()

        if (pipelineTmp.visibility != Visibility.PUBLIC)
            throw ForbiddenOperationException()

        return PipelineDTO(
            pipelineTmp.id,
            pipelineTmp.name,
            pipelineTmp.originalImage,
            pipelineTmp.transformations,
            pipelineTmp.visibility,
            pipelineTmp.userOwner,
            pipelineTmp.description,
            pipelineTmp.createdAt
        )
    }

    override fun getUserPipelines(username: String): List<PipelineDTO> {

        if (username.isBlank())
            throw InvalidRequestException()

        val userPipelines = app.getUserPipelines(username)
        var mutableList = mutableListOf<PipelineDTO>()
        for(pipelineTmp in userPipelines){
            mutableList.add(PipelineDTO(
                pipelineTmp.id,
                pipelineTmp.name,
                pipelineTmp.originalImage,
                pipelineTmp.transformations,
                pipelineTmp.visibility,
                pipelineTmp.userOwner,
                pipelineTmp.description,
                pipelineTmp.createdAt
            ))
        }
        return mutableList
    }

    override fun createPipeline(description: String, name: String, id: Long,
                                transformations: String): PipelineDTO {
        if (id<0 || description.isBlank() || name.isBlank()  || transformations.isEmpty()) {
            throw EmptyPipelineException()
        }

        val visibility = Visibility.PUBLIC

        val authentication = SecurityContextHolder.getContext().authentication

        val userOwner = authentication.name

        val pipelineDAO = PipelineDAO(
            id = 0,
            name = name,
            originalImage = id,
            transformations = transformations,
            visibility = visibility,
            userOwner = userOwner,
            description = description
        )

        val savedPipeline = app.createPipeline(pipelineDAO)

        return PipelineDTO(
            savedPipeline.id,
            savedPipeline.name,
            savedPipeline.originalImage,
            savedPipeline.transformations,
            savedPipeline.visibility,
            savedPipeline.userOwner,
            savedPipeline.description,
            savedPipeline.createdAt
        )
    }

    override fun changePipelineVisibility(id: Long, newVis: Int): PipelineDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name

        val pipeline = app.getPipeline(id)

        if (!pipeline.isPresent)
            throw PipelineNotFoundException()

        if(!pipeline.get().userOwner.equals(username))
            throw ForbiddenOperationException()

        val vis = when(newVis){
            0 -> Visibility.PRIVATE
            1 -> Visibility.FRIEND_ONLY
            2 -> Visibility.PUBLIC
            else -> {
                throw PipelineVisibilityDoesNotExist()
            }
        }
        val updated = app.updatePipelineVisibility(id, vis)
        return PipelineDTO(updated.id, updated.name, updated.originalImage,
            updated.transformations, vis, updated.userOwner, updated.description, updated.createdAt)
    }

}