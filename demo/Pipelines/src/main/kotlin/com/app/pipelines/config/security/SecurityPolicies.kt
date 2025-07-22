package com.app.pipelines.config

import com.app.pipelines.Data.PipelineRepository
import com.app.pipelines.Data.Visibility
import com.app.pipelines.config.filters.UserAuthToken
import com.app.pipelines.presentation.PipelineDTO
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.security.Principal


@Service
class capabilitiesService(val pipelineRepo: PipelineRepository) {

    val logger: org.slf4j.Logger = LoggerFactory.getLogger(capabilitiesService::class.java)

    fun canUpdatePipeline(user: Principal,id:Long): Boolean {
        val username = (user as UserAuthToken).name
        val pipeline = pipelineRepo.findById(id)
        if(pipeline.isPresent){
            val p = pipeline.get()
            return p.userOwner.equals(username)
        }
        return false
    }

    fun canReadPipeline(user:Principal,pipelineId:Long): Boolean {
        val authToken = (user as UserAuthToken)
        val p = pipelineRepo.findById(pipelineId)
        if(p.isPresent){
            val pipeline = p.get()
            return when (pipeline.visibility) {
                Visibility.PUBLIC -> true
                Visibility.FRIEND_ONLY -> authToken.friendsList.contains(pipeline.userOwner) || pipeline.userOwner.equals(authToken.name)
                Visibility.PRIVATE -> pipeline.userOwner.equals(authToken.name)
            }
        }
        return false
    }

    // In a post authorize
    fun canReadMultiplePipelines(user:Principal,pipelines:List<PipelineDTO>): Boolean {
        val authToken = (user as UserAuthToken)
        return pipelines.any { pipeline ->
            when (pipeline.visibility) {
                Visibility.PUBLIC -> true
                Visibility.FRIEND_ONLY -> authToken.friendsList.contains(pipeline.owner) || pipeline.owner.equals(authToken.name)
                Visibility.PRIVATE -> pipeline.owner.equals(authToken.name)
            }
        }
    }
}

@PreAuthorize("@capabilitiesService.canUpdatePipeline(principal, #id)")
annotation class CanUpdatePipeline

@PostFilter("@capabilitiesService.canReadMultiplePipelines(principal,filterObject)")
annotation class CanReadMultiplePipelines

@PreAuthorize("@capabilitiesService.canReadPipeline(principal,#id)")
annotation class CanReadPipeline
