package com.app.pipelines

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class PipelinesApplication

fun main(args: Array<String>) {
	runApplication<PipelinesApplication>(*args)
}
