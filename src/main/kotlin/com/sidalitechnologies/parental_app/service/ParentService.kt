package com.sidalitechnologies.parental_app.service

import com.sidalitechnologies.parental_app.common.toNonNullMap
import com.sidalitechnologies.parental_app.model.Parent
import com.sidalitechnologies.parental_app.model.Student
import com.sidalitechnologies.parental_app.model.dto_models.DTOParent
import com.sidalitechnologies.parental_app.repository.ParentRepository
import com.sidalitechnologies.parental_app.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
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

    fun createParent(parent: Parent): Parent {
        return parentRepository.save(parent)
    }

    fun getAll(pageable: Pageable): Page<Parent> {
        return parentRepository.findAll(pageable)
        //        val offset =pageable.pageNumber*pageable.pageSize
//        parentRepository.findPaginated(offset,pageable.pageSize).toList()
    }

    fun getByUsername(username: String): Parent? {
        return parentRepository.findByUserName(username)
    }

    fun getById(id: String): Parent? {
        return parentRepository.findById(id).orElse(null)
    }

    fun addStudent(username: String, student: Student): Boolean {
        val parent = getByUsername(username) ?: return false
        student.parentId = parent.id
        val studentCreated = runBlocking {
            studentService.createStudent(student)
        }
        println(studentCreated)
        return true
    }

    //through map
    fun updateParentMap(username: String, updateMap: Map<String, Any>): Boolean {
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
    fun updateParentDTO(username: String, dtoParent: DTOParent): Boolean {
        try {
            val parent = getByUsername(username) ?: return false
            val query = Query(Criteria.where("_id").`is`(parent.id))
            val update = Update()
            val fieldsMAp = dtoParent.toNonNullMap()
            fieldsMAp.forEach { (key, value) ->
                if (key != "id" && key != "userName" && key != "password")
                    update.set(key, value)
            }
            mongoTemplate.updateFirst(query, update, Parent::class.java)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    @Transactional
     fun deleteParent(userName: String): String {
        val parent = parentRepository.findByUserName(userName) ?: return "parent with $userName not found"
        if (parent.id != null) {
            studentService.deleteStudents(parent.id)
            parentRepository.delete(parent)
        }
        return "Parent and its students are deleted"
    }
}