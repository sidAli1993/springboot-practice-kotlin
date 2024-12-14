package com.sidalitechnologies.parental_app.repository

import com.sidalitechnologies.parental_app.model.Parent
import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component

@Component
interface ParentRepository :CoroutineCrudRepository<Parent,String> {
    suspend fun findByUserName(name: String): Parent?

    @Query("{}")
    fun findPaginated(skip:Int,limit:Int): Flow<Parent>
}