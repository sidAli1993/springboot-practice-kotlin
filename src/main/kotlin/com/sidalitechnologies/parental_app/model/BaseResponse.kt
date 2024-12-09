package com.sidalitechnologies.parental_app.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BaseResponse<T>(
    val status:String,
    val message:String,
    val token:String?=null,
    val data:T?=null,
)
