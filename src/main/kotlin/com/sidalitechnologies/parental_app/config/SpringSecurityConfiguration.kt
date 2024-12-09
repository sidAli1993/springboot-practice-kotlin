package com.sidalitechnologies.parental_app.config

import com.mongodb.client.MongoClients
import com.sidalitechnologies.parental_app.service.CustomUserDetailsServiceImpl
import com.sidalitechnologies.parental_app.service.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SpringSecurityConfiguration(
) {

    @Autowired
    lateinit var customUserDetailsServiceImpl: CustomUserDetailsServiceImpl
    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Value("\$spring.data.mongodb.uri")
    lateinit var mongoUri:String
    @Value("\$spring.data.mongodb.database")
    lateinit var databaseName:String

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/public/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()  // Other endpoints require authentication
            }
            .formLogin { login ->
                login.permitAll()  // Enable form-based login
            }
            .httpBasic { basic -> }  // HTTP Basic authentication
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        return http.getSharedObject(AuthenticationManagerBuilder::class.java)
            .authenticationProvider(authenticationProvider())
            .build()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(customUserDetailsServiceImpl) // Your custom user details service
        authProvider.setPasswordEncoder(passwordEncoder()) // Your password encoder (BCryptPasswordEncoder)
        return authProvider
    }

//    in spring boot auto configuration  no need to creatre mongo tempelate bean
//    @Bean
//    fun mongoTemplate():MongoTemplate{
//        val mongoClient = MongoClients.create(mongoUri)
//        val databaseFactory = SimpleMongoClientDatabaseFactory(mongoClient, databaseName)
//        return MongoTemplate(databaseFactory)
//    }


}
