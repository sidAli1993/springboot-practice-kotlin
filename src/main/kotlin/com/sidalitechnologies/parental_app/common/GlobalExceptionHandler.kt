package com.sidalitechnologies.parental_app.common

import com.sidalitechnologies.parental_app.model.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<BaseResponse<Any>> {
        val errors = ex.bindingResult.allErrors.associate { 
            it.objectName to (it.defaultMessage ?: "Validation error")
        }
        return ResponseEntity(
            BaseResponse(
                status = "failed",
                message = "Validation failed",
                data = errors
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<BaseResponse<Any>> {
        return ResponseEntity(
            BaseResponse(
                status = "failed",
                message = ex.message ?: "An error occurred"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}