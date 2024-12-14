package com.sidalitechnologies.parental_app.config

import kotlinx.coroutines.withContext
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.coroutines.CoroutineContext

suspend fun <T> withSecurityContext(block: suspend () -> T): T {
    val securityContext = SecurityContextHolder.getContext() // Get current security context
    return withContext(SecurityContextElement(securityContext)) {
        try {
            block()
        } finally {
            // Ensure context is cleared after execution
            SecurityContextHolder.clearContext()
        }
    }
}

class SecurityContextElement(
    private val securityContext: org.springframework.security.core.context.SecurityContext
) : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<SecurityContextElement>
    override val key: CoroutineContext.Key<*> = Key

    override fun toString(): String {
        return "SecurityContextElement(securityContext=$securityContext)"
    }

    init {
        // Set SecurityContext for this coroutine execution
        SecurityContextHolder.setContext(securityContext)
    }
}
