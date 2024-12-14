package com.sidalitechnologies.parental_app.service

import com.sidalitechnologies.parental_app.config.withSecurityContext
import com.sidalitechnologies.parental_app.repository.ParentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsServiceImpl(
) : UserDetailsService {

    @Autowired
    lateinit var userRepo: ParentRepository

    @Autowired
    lateinit var parentService: ParentService

    override fun loadUserByUsername(username: String?): UserDetails {

        val context = SecurityContextHolder.getContext()  // Get current SecurityContext

        // Ensure context is set before calling the service
        val obj = runBlocking {
            try {
                SecurityContextHolder.setContext(context)  // Set the context in runBlocking
                parentService.getByUsername(username ?: "")
                    ?: throw UsernameNotFoundException("Username not found: $username")
            } finally {
                SecurityContextHolder.clearContext()  // Clear context after the call
            }
        }
        println("After: ${SecurityContextHolder.getContext().authentication}")

        return User.builder()
            .username(obj.userName)
            .password(obj.password) // Make sure this is the encoded password stored in DB
            .roles(*(obj.roles?.map { it }?.toTypedArray() ?: arrayOf())) // Assuming roles is a collection of objects
            .build()
    }
}
