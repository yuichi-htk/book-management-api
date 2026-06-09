package com.example.book_management_api.exception

import com.example.book_management_api.dto.ErrorResponse
import com.example.book_management_api.dto.ValidationError
import com.example.book_management_api.dto.ValidationErrorResponse
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        val errors = exception.bindingResult.allErrors.map { error ->
            ValidationError(
                field = (error as? FieldError)?.field ?: error.objectName,
                reason = error.defaultMessage ?: "invalid value",
            )
        }

        return ResponseEntity.badRequest().body(ValidationErrorResponse(errors = errors))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(exception: ConstraintViolationException): ResponseEntity<ValidationErrorResponse> {
        val errors = exception.constraintViolations.map { violation ->
            ValidationError(
                field = violation.propertyPath.toString(),
                reason = violation.message,
            )
        }

        return ResponseEntity.badRequest().body(ValidationErrorResponse(errors = errors))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(exception: HttpMessageNotReadableException): ResponseEntity<ValidationErrorResponse> =
        ResponseEntity.badRequest().body(
            ValidationErrorResponse(
                errors = listOf(
                    ValidationError(
                        field = "request",
                        reason = exception.mostSpecificCause.message ?: "invalid request body",
                    ),
                ),
            ),
        )

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(exception: BadRequestException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(ErrorResponse(code = exception.code, message = exception.message))

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(exception: ResourceNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(code = exception.code, message = exception.message))

    @ExceptionHandler(BusinessRuleViolationException::class)
    fun handleBusinessRuleViolation(exception: BusinessRuleViolationException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(code = exception.code, message = exception.message))

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(code = "INTERNAL_SERVER_ERROR", message = "Internal server error."))
}
