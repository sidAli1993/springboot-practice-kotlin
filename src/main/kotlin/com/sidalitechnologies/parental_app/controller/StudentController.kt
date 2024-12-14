package com.sidalitechnologies.parental_app.controller

import com.sidalitechnologies.parental_app.common.buildResponse
import com.sidalitechnologies.parental_app.model.BaseResponse
import com.sidalitechnologies.parental_app.model.Student
import com.sidalitechnologies.parental_app.repository.ParentRepository
import com.sidalitechnologies.parental_app.service.ParentService
import com.sidalitechnologies.parental_app.service.StudentService
import jakarta.validation.Valid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException.NotFound

@RestController
@RequestMapping("v1/student")
class StudentController {

    @Autowired
    lateinit var studentService: StudentService

    @Autowired
    lateinit var parentRepository: ParentRepository

    @Autowired
    lateinit var parentService: ParentService

    @PostMapping("/create")
    fun addStudent(@Valid @RequestBody student: Student,bindingResult: BindingResult):ResponseEntity<BaseResponse<Any>>{
        val authentication=SecurityContextHolder.getContext().authentication
        if (bindingResult.hasErrors()) throw RuntimeException("validation failed ${bindingResult.allErrors.map { it.defaultMessage }}",)

        val parent=runBlocking {
            parentService.getByUsername(authentication.name)
        }
        if (parent==null) throw UsernameNotFoundException("Parent not found")

        student.parentId=parent.id
        runBlocking {
            studentService.createStudent(student)
        }

        return buildResponse(
            "success",
            "student successfully created",
            HttpStatus.CREATED,
            data = student
        )
    }
    @GetMapping("/get-student/{rollNo}")
    fun getStudentByRollNo(@PathVariable rollNo:String): ResponseEntity<BaseResponse<Any>>{
        if (rollNo.isEmpty()) throw RuntimeException("roll number is empty")
        val student= runBlocking {
            studentService.getStudentByRollNo(rollNo)
        }
        if (student==null) throw RuntimeException("no student exists on this roll number.")

        return buildResponse(
            "success",
            "student found successfully",
            HttpStatus.OK,
            data = student
        )
    }

    @GetMapping("/get-students")
    fun getStudentsByParent():ResponseEntity<BaseResponse<Any>>{
        val username=SecurityContextHolder.getContext().authentication.name
        val students=runBlocking {
            val parent= parentService.getByUsername(username) ?: throw RuntimeException("parent not found")
            return@runBlocking studentService.getStudentsByParent(parent.id ?: "")
        }
        if (students.isEmpty()) throw RuntimeException("No students found")

        return buildResponse(
            "success",
            "students found successfully",
            HttpStatus.OK,
            data = students
        )
    }

    @DeleteMapping("/delete-student")
    fun deleteStudents():ResponseEntity<BaseResponse<Any>>{
        val username=SecurityContextHolder.getContext().authentication.name
        val isDeleted= runBlocking {
            val parent=parentService.getByUsername(username) ?: throw RuntimeException("parent not found")
            return@runBlocking studentService.deleteStudents(parent.id ?: "")
        }
        if (!isDeleted) throw RuntimeException("Something went wrong")

        return buildResponse(
            "success",
            "students deleted successfully",
            HttpStatus.OK,
        )
    }
}