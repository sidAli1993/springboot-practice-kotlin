package com.sidalitechnologies.parental_app.controller

import com.sidalitechnologies.parental_app.common.buildResponse
import com.sidalitechnologies.parental_app.model.BaseResponse
import com.sidalitechnologies.parental_app.model.Student
import com.sidalitechnologies.parental_app.repository.ParentRepository
import com.sidalitechnologies.parental_app.service.ParentService
import com.sidalitechnologies.parental_app.service.StudentService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/student")
class StudentController {

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var parentRepository: ParentRepository

    @Autowired
    lateinit var parentService: ParentService

}