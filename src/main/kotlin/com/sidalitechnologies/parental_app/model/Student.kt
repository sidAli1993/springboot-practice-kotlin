package com.sidalitechnologies.parental_app.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Student(
    @Id
    val id: String? = ObjectId.get().toHexString(),
    @Indexed(unique = true)
    @field:NotNull(message = "RollNo is empty")
    @field:Size(min = 4, max = 9, message = "rollNo should be greater than 3 and less than 10")
    val rollNo: String,
    @field:NotNull(message = "name is empty")
    val name: String,
    @field:NotNull(message = "className is empty")
    val className: String,
    @field:NotNull(message = "parentName is empty")
    val parentName: String,
    val address: String?,
    val isAdmitted: Boolean?,
    val majorSubject: String?,
    val phone: String?,
    @field:NotNull(message = "Email must not be empty")
    val email: String,
    var parentId:String?=null
)
