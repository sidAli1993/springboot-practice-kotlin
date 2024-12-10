package com.sidalitechnologies.parental_app.repository

import com.sidalitechnologies.parental_app.model.Parent
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component

@Component
interface ParentRepository :MongoRepository<Parent,String> {
    fun findByUserName(name: String): Parent?

//    @Query("{}")
//    fun findPaginated(skip:Int,limit:Int): Flow<Parent>
}