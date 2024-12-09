package com.sidalitechnologies.parental_app.service

import com.sidalitechnologies.parental_app.model.Student
import com.sidalitechnologies.parental_app.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
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

    suspend fun createMultiStudents(stdList: List<Student>):List<Student> = withContext(Dispatchers.IO){
        stdRepo.saveAll(stdList)
    }

    suspend fun createStudent(student: Student):Student = withContext(Dispatchers.IO){
        stdRepo.save(student)
    }

    private suspend fun getStudentByRollNo(rollNo:String):Student? = withContext(Dispatchers.IO){
        val query = Query(Criteria.where("rollNo").`is`(rollNo))
        if (!mongoTemplate.exists(query,Student::class.java))
            return@withContext null

        return@withContext mongoTemplate.findOne(query,Student::class.java)
    }

    suspend fun deleteStudent(rollNo:String):Boolean = withContext(Dispatchers.IO){
        val student=getStudentByRollNo(rollNo) ?: return@withContext false
        studentRepository.delete(student)
        true
    }

}