package com.app.pipelines.presentation

import com.app.pipelines.Data.Visibility
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(name="Pipeline")
data class PipelineDTO(
    val id: Long,
    val name: String,
    val originalImage: Long?,
    val transformations: String,
    val visibility: Visibility,
    val owner: String,
    val description: String,
    val createdAt: Instant?
)
