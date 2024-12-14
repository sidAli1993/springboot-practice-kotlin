package com.sidalitechnologies.parental_app.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskDecorator
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class AsyncConfig( @Qualifier("securityContextTaskDecorator") private val taskDecorator: TaskDecorator) {

    @Bean(name = ["taskExecutor"])
    fun taskExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.setCorePoolSize(10) // Minimum number of threads
        executor.setMaxPoolSize(20) // Maximum number of threads
        executor.setQueueCapacity(500) // Queue capacity before new threads are spawned
        executor.setThreadNamePrefix("AsyncExecutor-") // Thread name prefix for easy debugging
        executor.setTaskDecorator(taskDecorator) // Use the TaskDecorator bean
        executor.initialize()
        return executor
    }
}
