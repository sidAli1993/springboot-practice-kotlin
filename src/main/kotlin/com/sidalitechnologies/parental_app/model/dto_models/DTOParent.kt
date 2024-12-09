package com.sidalitechnologies.parental_app.model.dto_models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sidalitechnologies.parental_app.model.Student
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.bson.types.ObjectId
import org.springframework.boot.convert.DataSizeUnit
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef

data class DTOParent(
    @field:NotNull(message = "name should not be empty")
    val name:String,
    @field:NotNull(message ="Phone must not be null")
    @field:Size(min = 9, max = 11, message = "Phone size must be greater than 8 or less than 12")
    val phone:String,
    val student:MutableList<Student>?,
    val roles:MutableList<String>?
)
