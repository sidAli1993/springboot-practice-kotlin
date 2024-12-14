package com.sidalitechnologies.parental_app.controller

import com.fasterxml.jackson.databind.ser.Serializers.Base
import com.sidalitechnologies.parental_app.common.buildResponse
import com.sidalitechnologies.parental_app.config.withSecurityContext
import com.sidalitechnologies.parental_app.model.BaseResponse
import com.sidalitechnologies.parental_app.model.Parent
import com.sidalitechnologies.parental_app.model.Student
import com.sidalitechnologies.parental_app.model.dto_models.DTOParent
import com.sidalitechnologies.parental_app.repository.ParentRepository
import com.sidalitechnologies.parental_app.service.JwtTokenProvider
import com.sidalitechnologies.parental_app.service.ParentService
import jakarta.validation.Valid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
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
    fun addStudent(@Valid @RequestBody student: Student,bindingResult: BindingResult): ResponseEntity<BaseResponse<Any>> {
        if (bindingResult.hasErrors()){
            val errors=bindingResult.allErrors.map { it.defaultMessage }
            return buildResponse(
                "failed",
                "validation errors",
                HttpStatus.BAD_REQUEST,
                data = errors
            )
        }
        val authenticated = SecurityContextHolder.getContext().authentication
        val isAdded = runBlocking {  parentService.addStudent(authenticated.name, student) }
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
    fun updateParent(
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


        val isUpdated = runBlocking {  parentService.updateParentDTO(auth.name, dtoParent) }
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
            data = runBlocking {  parentService.getByUsername(auth.name) }
        )
    }

    @GetMapping("/findAll")
      fun findAll(@RequestParam page:Int=0,size:Int=10): ResponseEntity<BaseResponse<Any>> {
        val pageableData = runBlocking(Dispatchers.IO) {  parentService.getAll(page,size) }
        val totalElements= runBlocking { parentRepository.count() }
        val totalPages=totalElements/size
        val offset=page*size
        val isLast=page<=totalPages
        val responseMap = mapOf(
            "isLast" to isLast,
            "currentPage" to page,
            "totalPages" to totalPages,
            "totalSize" to totalElements,
            "parentList" to pageableData
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
     fun getByUserName(@PathVariable username: String): ResponseEntity<BaseResponse<Any>>  {
        println("parent controlller Before: ${SecurityContextHolder.getContext().authentication}")
        val user = runBlocking {
             parentService.getByUsername(username)
        }
        
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
    fun getById(@PathVariable id: String): ResponseEntity<BaseResponse<Any>> {
        val user = runBlocking(Dispatchers.IO) {  parentService.getById(id) }
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
    fun deleteParent(@PathVariable userName: String): ResponseEntity<BaseResponse<Any>> {
        if (userName.isEmpty())
            return buildResponse(
                "failed",
                "Username is empty",
                HttpStatus.BAD_REQUEST
            )
        val message = runBlocking(Dispatchers.IO) {  parentService.deleteParent(userName) }

        return buildResponse(
            "success",
            message,
            HttpStatus.OK
        )
    }
}