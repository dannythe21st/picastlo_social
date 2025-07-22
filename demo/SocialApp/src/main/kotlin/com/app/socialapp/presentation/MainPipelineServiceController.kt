package com.app.socialapp.presentation

import com.app.socialapp.application.MainPipelineServiceApp
import com.app.socialapp.data.PipelineDTO
import org.springframework.web.bind.annotation.RestController

@RestController
class MainPipelineServiceController(val app: MainPipelineServiceApp): MainPipelineServiceAPI{
    override fun getPipeline(id: Long): PipelineDTO {
        return app.getPipeline(id)
    }

    override fun getUserPipelines(email: String): List<PipelineDTO> {
        return app.getUserPipelines(email)
    }

    override fun getPublicPipeline(id: Long): PipelineDTO {
        return app.getPublicPipeline(id)
    }

    override fun createPipeline(description: String, name: String, id: Long,transformations : String): PipelineDTO {
        return app.createPipeline(description, name, id,transformations)
    }

    override fun changePipelineVisibility(id: Long, newVis: Int): PipelineDTO {
        return app.changePipelineVisibility(id, newVis)
    }

}