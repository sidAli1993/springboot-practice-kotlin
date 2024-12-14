package com.sidalitechnologies.parental_app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskDecorator
import org.springframework.security.core.context.SecurityContextHolder

@Configuration
class TaskDecoratorConfig : TaskDecorator {

    override fun decorate(runnable: Runnable): Runnable {
        // Capture the current security context
        val securityContext = SecurityContextHolder.getContext()
        return Runnable {
            try {
                // Set the captured context for this thread
                SecurityContextHolder.setContext(securityContext)
                runnable.run()
            } finally {
                // Clear the context after the task to avoid leaks
                SecurityContextHolder.clearContext()
            }
        }
    }

    @Bean(name = ["securityContextTaskDecorator"])
    fun securityContextTaskDecorator(): TaskDecorator {
        return this
    }
}
