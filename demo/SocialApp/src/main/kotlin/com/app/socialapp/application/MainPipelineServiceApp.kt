package com.app.socialapp.application

import com.app.socialapp.data.PipelineDTO
import com.app.socialapp.service.*
import feign.FeignException
import org.springframework.stereotype.Component
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@Component
class MainPipelineServiceApp(val pipelineService: PipelineService) {

    fun getPipeline(pipelineID : Long): PipelineDTO{
        return try{
            pipelineService.getPipeline(pipelineID)
        } catch (ex:FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun getUserPipelines(userEmail : String) : List<PipelineDTO>{
        return try{
            pipelineService.getUserPipelines(userEmail)
        } catch (ex:FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun getPublicPipeline(pipelineID : Long): PipelineDTO{
        return try{
            pipelineService.getPublicPipeline(pipelineID)
        } catch (ex:FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), ex.message)
        }
    }

    fun createPipeline(description: String, name: String, id: Long,transformations :String): PipelineDTO{
        return try{
            pipelineService.createPipeline(
                description = description,
                name = name,
                id = id,
                transformations = transformations,
            )
        } catch (ex:FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }

    fun changePipelineVisibility(@PathVariable("id") id: Long, newVis: Int): PipelineDTO{
        try{
            return pipelineService.changePipelineVisibility(id, newVis)
        } catch (ex:FeignException){
            throw ResponseStatusException(HttpStatus.valueOf(ex.status()), "Please check micro service console")
        }
    }
}