package com.sidalitechnologies.parental_app.controller

import com.sidalitechnologies.parental_app.common.buildResponse
import com.sidalitechnologies.parental_app.model.BaseResponse
import com.sidalitechnologies.parental_app.repository.ParentRepository
import com.sidalitechnologies.parental_app.repository.StudentRepository
import com.sidalitechnologies.parental_app.service.ParentService
import com.sidalitechnologies.parental_app.service.StudentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController {

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var parentRepository: ParentRepository

    @Autowired
    lateinit var parentService: ParentService

    @Autowired
    lateinit var studentService: StudentService

    @GetMapping("/getAll")
    fun findAll(@RequestParam page: Int = 0, size: Int = 10): ResponseEntity<BaseResponse<Any>> {
        val pageableData = runBlocking(Dispatchers.IO) { parentService.getAll(page, size) }
        val totalElements = runBlocking { parentRepository.count() }
        val totalPages = totalElements / size
        val offset = page * size
        val isLast = page <= totalPages
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
}