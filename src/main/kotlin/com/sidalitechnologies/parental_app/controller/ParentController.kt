package com.sidalitechnologies.parental_app.controller

import com.fasterxml.jackson.databind.ser.Serializers.Base
import com.sidalitechnologies.parental_app.common.buildResponse
import com.sidalitechnologies.parental_app.model.BaseResponse
import com.sidalitechnologies.parental_app.model.Parent
import com.sidalitechnologies.parental_app.model.Student
import com.sidalitechnologies.parental_app.model.dto_models.DTOParent
import com.sidalitechnologies.parental_app.repository.ParentRepository
import com.sidalitechnologies.parental_app.service.JwtTokenProvider
import com.sidalitechnologies.parental_app.service.ParentService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class ParentController {

    @Autowired
    lateinit var parentService: ParentService

    @Autowired
    lateinit var parentRepository: ParentRepository

    @PostMapping("/add-student")
    suspend fun addStudent(@RequestBody student: Student): ResponseEntity<BaseResponse<Any>> {
        val authenticated = SecurityContextHolder.getContext().authentication
        val isAdded = parentService.addStudent(authenticated.name, student)
        if (!isAdded) {
            return buildResponse(
                "failed",
                "Unable to add student",
                HttpStatus.BAD_REQUEST
            )
        }
        return buildResponse(
            "success",
            "student added successfully",
            HttpStatus.CREATED,
            data = student
        )
    }

    @PutMapping("/update-parent")
    suspend fun updateParent(
        @Valid @RequestBody dtoParent: DTOParent,
        bindingResult: BindingResult
    ): ResponseEntity<BaseResponse<Any>> {
        val auth = SecurityContextHolder.getContext().authentication
        if (bindingResult.hasErrors()) {
            val errors = bindingResult.allErrors.map { it.defaultMessage }
            return buildResponse(
                "failed",
                "Validation failed",
                HttpStatus.BAD_REQUEST,
                data = errors
            )
        }


        val isUpdated = parentService.updateParentDTO(auth.name, dtoParent)
        if (!isUpdated)
            return buildResponse(
                "failed",
                "unable to update",
                HttpStatus.BAD_REQUEST
            )

        return buildResponse(
            "success",
            "Parent has been updated",
            HttpStatus.CREATED,
            data = parentService.getByUsername(auth.name)
        )
    }

    @GetMapping("/findAll")
    suspend fun findAll(@RequestParam page:Int=0,size:Int=10): ResponseEntity<BaseResponse<Any>> {
        val pageableData = parentService.getAll(PageRequest.of(page,size))

        val responseMap = mapOf(
            "isLast" to pageableData.isLast,
            "currentPage" to pageableData.pageable.pageNumber,
            "totalPages" to pageableData.totalPages,
            "totalSize" to pageableData.totalElements,
            "parentList" to pageableData.content
        )
        return buildResponse(
            "success",
            "",
            HttpStatus.OK,
            null,
            responseMap
        )
    }

    @GetMapping("/getByUsername/{username}")
    suspend fun getByUserName(@PathVariable username: String): ResponseEntity<BaseResponse<Any>> {
        val user = parentService.getByUsername(username)
        return if (user != null) {
            buildResponse(
                "success",
                "",
                HttpStatus.OK,
                data = user
            )
        } else {
            buildResponse(
                "failed",
                "user not found",
                HttpStatus.NOT_FOUND,
            )
        }
    }


    @GetMapping("/getById/{id}")
    suspend fun getById(@PathVariable id: String): ResponseEntity<BaseResponse<Any>> {
        val user = parentService.getById(id)
        return if (user != null) {
            buildResponse(
                "success",
                "",
                HttpStatus.OK,
                data = user
            )
        } else {
            buildResponse(
                "failed",
                "user not found",
                HttpStatus.NOT_FOUND,
            )
        }
    }

    @DeleteMapping("/delete-parent/{userName}")
    suspend fun deleteParent(@PathVariable userName: String): ResponseEntity<BaseResponse<Any>> {
        if (userName.isEmpty())
            return buildResponse(
                "failed",
                "Username is empty",
                HttpStatus.BAD_REQUEST
            )
        val message = parentService.deleteParent(userName)

        return buildResponse(
            "success",
            message,
            HttpStatus.OK
        )
    }
}