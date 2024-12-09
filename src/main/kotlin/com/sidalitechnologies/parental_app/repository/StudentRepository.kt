package com.sidalitechnologies.parental_app.repository

import com.sidalitechnologies.parental_app.model.Student
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component

@Component
interface StudentRepository:MongoRepository<Student,String> {

}