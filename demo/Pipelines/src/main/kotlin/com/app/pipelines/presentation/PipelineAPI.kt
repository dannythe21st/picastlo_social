package com.app.pipelines.presentation

import com.app.pipelines.config.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/pipeline")
interface PipelineAPI {

    @GetMapping("/{id}")
    @CanReadPipeline
    fun getPipeline(@PathVariable("id") id: Long): PipelineDTO

    @GetMapping("/public/{id}")
    fun getPublicPipeline(@PathVariable("id") id: Long): PipelineDTO

    @GetMapping("/all/{username}")
    @CanReadMultiplePipelines //try with read multiple filter
    fun getUserPipelines(@PathVariable("username") username: String): List<PipelineDTO>

    @PostMapping("", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPipeline(@RequestParam("description") description: String,
                       @RequestParam("name") name: String,
                       @RequestParam("id") id: Long,
                       @RequestPart("transformations") transformations: String): PipelineDTO

    /**
     * the new visibility can either be 0,1 or 2.
     * These match to private, friend-only or public, respectively
     */
    @PutMapping("/{id}")
    @CanUpdatePipeline
    fun changePipelineVisibility(@PathVariable("id") id: Long,
                                 @RequestParam("vis") newVis: Int): PipelineDTO
}