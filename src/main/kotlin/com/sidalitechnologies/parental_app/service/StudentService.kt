package com.sidalitechnologies.parental_app.service

import com.sidalitechnologies.parental_app.model.Student
import com.sidalitechnologies.parental_app.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
class StudentService {

@Autowired
lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var stdRepo: StudentRepository

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

     suspend fun createStudent(student: Student):Student?{
        return  stdRepo.save(student)
    }

    suspend fun getStudentByRollNo(rollNo:String):Student?{
        val student= stdRepo.findByRollNo(rollNo) ?: return null
        return student
    }

    suspend fun getStudentsByParent(parentId:String):List<Student>{
        val students=stdRepo.findAllByParentId(parentId).toList()
        if (students.isEmpty())
            return emptyList()

        return students
    }

    suspend fun deleteStudents(parentId:String):Boolean {
        val students=getStudentsByParent(parentId)
        if (students.isEmpty())
            return false

        stdRepo.deleteAll(students)
        return true
    }

}