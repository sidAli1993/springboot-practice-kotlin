package com.sidalitechnologies.parental_app.repository

import com.sidalitechnologies.parental_app.model.Student
import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component

@Component
interface StudentRepository:MongoRepository<Student,String> {
     fun findAllByParentId(parentId:String):List<Student>
     fun findByRollNo(rollNo:String):Student?
}