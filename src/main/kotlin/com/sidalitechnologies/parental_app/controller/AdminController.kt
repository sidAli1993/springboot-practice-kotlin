package com.sidalitechnologies.parental_app.controller

import com.sidalitechnologies.parental_app.common.buildResponse
import com.sidalitechnologies.parental_app.model.BaseResponse
import com.sidalitechnologies.parental_app.repository.ParentRepository
import com.sidalitechnologies.parental_app.repository.StudentRepository
import com.sidalitechnologies.parental_app.service.ParentService
import com.sidalitechnologies.parental_app.service.StudentService
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
     fun getAllUsers(@RequestParam page:Int=0,size:Int=10):ResponseEntity<BaseResponse<Any>>{
        val pageableData=parentService.getAll(PageRequest.of(page,size))

        val responseMap = mapOf(
            "isLast" to pageableData.isLast,
            "currentPage" to pageableData.pageable.pageNumber,
            "totalPages" to pageableData.totalPages,
            "totalSize" to pageableData.totalElements,
            "parentList" to pageableData.content
        )

        return buildResponse(
            "success",
            "parent list found",
            HttpStatus.OK,
            data = responseMap
        )
    }
}