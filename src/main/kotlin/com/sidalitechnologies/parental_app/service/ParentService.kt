package com.sidalitechnologies.parental_app.service

import com.sidalitechnologies.parental_app.common.toNonNullMap
import com.sidalitechnologies.parental_app.model.Parent
import com.sidalitechnologies.parental_app.model.Student
import com.sidalitechnologies.parental_app.model.dto_models.DTOParent
import com.sidalitechnologies.parental_app.repository.ParentRepository
import com.sidalitechnologies.parental_app.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.apache.coyote.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class ParentService {

    @Autowired
    private lateinit var parentRepository: ParentRepository

    @Autowired
    private lateinit var studentService: StudentService

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    suspend fun createParent(parent: Parent): Parent = withContext(Dispatchers.IO) {
        studentService.createMultiStudents(parent.student?.toList() ?: emptyList())
        return@withContext parentRepository.save(parent)
    }

    suspend fun getAll(pageable: Pageable): Page<Parent> = withContext(Dispatchers.IO){
        parentRepository.findAll(pageable)
    }

    suspend fun getByUsername(username: String): Parent? = withContext(Dispatchers.IO) {
         parentRepository.findByUserName(username)
    }

    suspend fun getById(id: String): Parent? = withContext(Dispatchers.IO) {
        parentRepository.findById(id).orElse(null)
    }

    suspend fun addStudent(username: String, student: Student): Boolean = withContext(Dispatchers.IO){
        val parent = getByUsername(username) ?: return@withContext false
        studentService.createStudent(student)
        val existingStudents = parent.student ?: mutableListOf()
        existingStudents.add(student)
        parent.student = existingStudents
        createParent(parent)
        return@withContext true
    }

    //through map
    suspend fun updateParentMap(username: String, updateMap: Map<String, Any>): Boolean {
        val parent = getByUsername(username) ?: return false
        val query = Query(Criteria.where("_id").`is`(parent.id))
        val update = Update()
        updateMap.forEach { (key, value) ->
            update.set(key, value)
        }
        mongoTemplate.updateFirst(query, update, Parent::class.java)
        return true
    }

    @Transactional
    suspend fun updateParentDTO(username: String, dtoParent: DTOParent): Boolean = withContext(Dispatchers.IO){
        try {
            val parent = getByUsername(username) ?: return@withContext false
            val query = Query(Criteria.where("_id").`is`(parent.id))
            val update = Update()
            val fieldsMAp = dtoParent.toNonNullMap()
            fieldsMAp.forEach { (key, value) ->
                if (key != "id" && key != "userName" && key != "password" && key != "student")
                    update.set(key, value)
            }
            mongoTemplate.updateFirst(query, update, Parent::class.java)
            if (dtoParent.student == null) {
                throw RuntimeException("Student record is empty")
            }
            dtoParent.student?.forEach { std ->
                val queryStd = Query(Criteria.where("rollNo").`is`(std.rollNo))
                if (!mongoTemplate.exists(queryStd, Student::class.java)) {
                    return@withContext false
                }
                val stdMap = std.toNonNullMap()
                stdMap.forEach { (key, value) ->
                    if (key != "id")
                        update.set(key, value)
                }
                mongoTemplate.updateFirst(queryStd, update, Student::class.java)
            }
            return@withContext true
        } catch (e: Exception) {
            return@withContext false
        }
    }

    @Transactional
    suspend fun deleteParent(userName: String): String = withContext(Dispatchers.IO) {
        val parent = parentRepository.findByUserName(userName) ?: return@withContext "parent with $userName not found"
        val failedDeletedStudents = mutableListOf<Student>()
        parent.student?.forEach { std ->
            val isDeleted = studentService.deleteStudent(std.rollNo)
            if (!isDeleted)
                failedDeletedStudents.add(std)
        }
        if (failedDeletedStudents.isNotEmpty())
            return@withContext "Some students are failed to deleted $failedDeletedStudents"

        parentRepository.delete(parent)
        "Parent and its students are deleted"
    }
}