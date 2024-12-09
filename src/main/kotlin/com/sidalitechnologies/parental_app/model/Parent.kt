package com.sidalitechnologies.parental_app.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import java.time.LocalDate
import java.time.LocalDateTime

data class Parent(
    @Id
    val id:String? = ObjectId.get().toHexString(),
    val name:String,
    val phone:String,
    @Indexed(unique = true)
    val userName:String,
//    json ignore will completely ignore even on writing data
//    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password:String,
//    just to save id instead of object
    @DBRef
    var student:MutableList<Student>?,
    val roles:MutableList<String>?,
    var createdDate:LocalDateTime?=null,
    var lastModifiedDate:LocalDateTime?=null
)
