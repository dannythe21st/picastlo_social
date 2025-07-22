package com.app.pipelines.application

import com.app.pipelines.Data.PipelineDAO
import com.app.pipelines.Data.PipelineRepository
import com.app.pipelines.Data.Visibility
import com.app.pipelines.presentation.PipelineDTO
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class PipelineApplication(val repo: PipelineRepository) {

    fun createPipeline(newPipeline : PipelineDAO) : PipelineDAO {
        return repo.save(newPipeline)
    }

    fun getUserPipelines(userEmail: String): Iterable<PipelineDAO>{
        return repo.findAllByUserOwner(userEmail)
    }

    fun getPipeline(pipelineID : Long): Optional<PipelineDAO>{
        return repo.findById(pipelineID)
    }

    fun updatePipelineVisibility(id: Long, newVis: Visibility): PipelineDAO {
        val pipeline = repo.findById(id).get()
        pipeline.visibility = newVis
        return repo.save(pipeline)
    }
}