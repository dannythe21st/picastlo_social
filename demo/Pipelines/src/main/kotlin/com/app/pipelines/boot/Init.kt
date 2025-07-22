package com.app.pipelines.boot

import com.app.pipelines.Data.PipelineDAO
import com.app.pipelines.Data.PipelineRepository
import com.app.pipelines.Data.Visibility
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Paths

@Component
class Init(val pipelines: PipelineRepository): CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        val exampleTransformations1 = "{\"transformations\":[{\"name\":\"Convolution(sharpen)\",\"options\":[{\"name\":\"Iterations\",\"value\":22,\"min\":1,\"max\":25},{\"name\":\"Opacity\",\"value\":16,\"min\":0,\"max\":100}],\"kernel\":[[0,-1,0],[-1,5,-1],[0,-1,0]]},{\"name\":\"Point Wise Transformation\",\"options\":[{\"name\":\"Opacity\",\"value\":57,\"min\":0,\"max\":100}]},{\"name\":\"Quantize\",\"options\":[{\"name\":\"k\",\"value\":14,\"min\":1,\"max\":20},{\"name\":\"percentage\",\"value\":35,\"min\":1,\"max\":100},{\"name\":\"Opacity\",\"value\":38,\"min\":0,\"max\":100}]}]}"
        val mockpipelines = listOf(
            PipelineDAO(0, "Pipeline 1", 1, exampleTransformations1, Visibility.PUBLIC, "rcosta", "Mock description 1"),
            PipelineDAO(0, "Pipeline 2", 2,exampleTransformations1 , Visibility.PUBLIC, "deugenio", "Mock description 2"),
            PipelineDAO(0, "Pipeline 3", null, exampleTransformations1, Visibility.FRIEND_ONLY, "rcosta", "Mock description 3"),
            PipelineDAO(0, "Pipeline 4", 3, exampleTransformations1, Visibility.PUBLIC, "deugenio", "Mock description 4"),
            PipelineDAO(0, "Pipeline 5", 3, exampleTransformations1, Visibility.PRIVATE, "rcosta", "Mock description 5"),
            PipelineDAO(0, "Pipeline 6", 3, exampleTransformations1, Visibility.FRIEND_ONLY, "deugenio", "Mock description 6"),
            PipelineDAO(0, "Pipeline 7", 3, exampleTransformations1, Visibility.PUBLIC, "rcosta", "Mock description 7"),
            PipelineDAO(0, "Pipeline 8", 3, exampleTransformations1, Visibility.PRIVATE, "deugenio", "Mock description 8"),
            PipelineDAO(0, "Pipeline 9", null, exampleTransformations1, Visibility.FRIEND_ONLY, "rcosta", "Mock description 9"),
            PipelineDAO(0, "Pipeline 10", null, exampleTransformations1, Visibility.PUBLIC, "deugenio", "Mock description 10"),
            PipelineDAO(0, "Pipeline 11", null, exampleTransformations1, Visibility.PRIVATE, "rcosta", "Mock description 11"),
            PipelineDAO(0, "Pipeline 12", 5, exampleTransformations1, Visibility.FRIEND_ONLY, "deugenio", "Mock description 12"),
            PipelineDAO(0, "Pipeline 13", 5, exampleTransformations1, Visibility.PUBLIC, "rcosta", "Mock description 13"),
            PipelineDAO(0, "Pipeline 14", 5, exampleTransformations1, Visibility.PRIVATE, "deugenio", "Mock description 14"),
            PipelineDAO(0, "Pipeline 15", 5, exampleTransformations1, Visibility.FRIEND_ONLY, "rcosta", "Mock description 15"),
            PipelineDAO(0, "Pipeline 16", 6, exampleTransformations1, Visibility.PUBLIC, "deugenio", "Mock description 16"),
            PipelineDAO(0, "Pipeline 17", null, exampleTransformations1, Visibility.PRIVATE, "rcosta", "Mock description 17"),
            PipelineDAO(0, "Pipeline 18", 7, exampleTransformations1, Visibility.FRIEND_ONLY, "deugenio", "Mock description 18"),
            PipelineDAO(0, "Pipeline 19", 9, exampleTransformations1, Visibility.PUBLIC, "rcosta", "Mock description 19"),
            PipelineDAO(0, "Pipeline 20", null, exampleTransformations1, Visibility.PRIVATE, "deugenio", "Mock description 20"),
            PipelineDAO(0, "Pipeline 21", 11, exampleTransformations1, Visibility.PRIVATE, "deugenio", "Mock description 21")
        )

        pipelines.saveAll(mockpipelines)
    }
}