package com.sidalitechnologies.parental_app.controller

import com.fasterxml.jackson.databind.ser.Serializers.Base
import com.sidalitechnologies.parental_app.common.buildResponse
import com.sidalitechnologies.parental_app.model.BaseResponse
import com.sidalitechnologies.parental_app.model.Parent
import com.sidalitechnologies.parental_app.model.dto_models.LoginRequest
import com.sidalitechnologies.parental_app.repository.ParentRepository
import com.sidalitechnologies.parental_app.service.JwtTokenProvider
import com.sidalitechnologies.parental_app.service.ParentService
import org.apache.coyote.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public")
class PublicController {

    @Autowired
    lateinit var parentService: ParentService

    @Autowired
    lateinit var parentRepository: ParentRepository

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    lateinit var authenticationManager: AuthenticationManager
    private val passwordEncoder = BCryptPasswordEncoder()


    @PostMapping("/create")
    fun createParent(@RequestBody parent: Parent): ResponseEntity<BaseResponse<Any>> {
        if (parentRepository.findByUserName(parent.userName) != null) {
            return buildResponse(
                status = "failed",
                message = "Username already exists",
                token = null,
                data = null,
                httpStatus = HttpStatus.BAD_REQUEST
            )
        }
        val oldPassword = parent.password
        parent.password = passwordEncoder.encode(parent.password)
        parentService.createParent(parent)

        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(parent.userName, oldPassword)
        )
        val token = jwtTokenProvider.generateToken(authentication)

        return buildResponse(
            "success",
            "Parent has been created",
            token = token,
            data = parent,
            httpStatus = HttpStatus.CREATED
        )
    }

    @PostMapping("/login")
    fun doLogin(@RequestBody loginRequest: LoginRequest): ResponseEntity<BaseResponse<Any>> {
        if (loginRequest.userName.isNotEmpty())
            if (parentRepository.findByUserName(loginRequest.userName) == null)
                return buildResponse(
                    "failed",
                    "User not found",
                    HttpStatus.NOT_FOUND
                )
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.userName, loginRequest.password)
        )
        if (authentication.isAuthenticated) {
            val user = parentRepository.findByUserName(loginRequest.userName)
            val token = jwtTokenProvider.generateToken(authentication)
            return buildResponse(
                "success",
                "user log in successfully",
                token = token,
                data = user,
                httpStatus = HttpStatus.OK
            )
        }
        return buildResponse(
            "failed",
            "Authentication failed",
            HttpStatus.UNAUTHORIZED
        )
    }
}