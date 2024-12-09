package com.sidalitechnologies.parental_app.service

import com.sidalitechnologies.parental_app.repository.ParentRepository
import org.springframework.beans.factory.annotation.Autowired
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

    override fun loadUserByUsername(username: String?): UserDetails {
        val obj = userRepo.findByUserName(username ?: "")
            ?: throw UsernameNotFoundException("Username not found: $username")

        return User.builder()
            .username(obj.userName)
            .password(obj.password) // Make sure this is the encoded password stored in DB
            .roles(*(obj.roles?.map { it }?.toTypedArray() ?: arrayOf())) // Assuming roles is a collection of objects
            .build()
    }
}
