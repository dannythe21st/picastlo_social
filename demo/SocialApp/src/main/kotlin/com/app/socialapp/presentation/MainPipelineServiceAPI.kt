package com.app.socialapp.presentation

import com.app.socialapp.data.PipelineDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RequestMapping("")
@Tag(name = "Pipelines", description = "Pipelines API")
interface MainPipelineServiceAPI {

    @GetMapping("/pipelines/{id}")
    @Operation(summary = "Get a pipeline by Id")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Pipeline with requested id"),
        ApiResponse(responseCode = "400", description = "Invalid Id"),
        ApiResponse(responseCode = "404", description = "Pipeline not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getPipeline(@PathVariable("id") id: Long): PipelineDTO

    @GetMapping("/users/{username}/pipelines")
    @Operation(summary = "Get a list of pipelines from a user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of user pipelines returned successfully"),
        ApiResponse(responseCode = "400", description = "Invalid username"),
        ApiResponse(responseCode = "404", description = "User not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getUserPipelines(@PathVariable("username") email: String): List<PipelineDTO>

    @GetMapping("/public-pipelines/{id}")
    @Operation(summary = "Get a public pipeline - made for unauthorized users")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of public pipelines returned successfully"),
        ApiResponse(responseCode = "400", description = "Invalid Id"),
        ApiResponse(responseCode = "404", description = "Pipeline not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getPublicPipeline(@PathVariable("id") id: Long): PipelineDTO

    @PostMapping("/pipelines", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Create a pipeline")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Pipeline created successfully"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun createPipeline(@RequestParam("description") description: String,
                       @RequestParam("name") name: String,
                       @RequestParam("id") id: Long,
                       @RequestPart("transformations") transformations: String,
    ): PipelineDTO


    /**
     * the new visibility can either be 0,1 or 2.
     * These match to private, friend-only or public, respectively
     */
    @PutMapping("/{id}")
    @Operation(summary = "Change pipeline visibility")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Pipeline updated successfully"),
        ApiResponse(responseCode = "404", description = "Pipeline not found"),
        ApiResponse(responseCode = "403", description = "Only the owner can change the pipeline visibility"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun changePipelineVisibility(@PathVariable("id") id: Long, newVis : Int): PipelineDTO


}