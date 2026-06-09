package com.example.book_management_api.dto

data class ValidationErrorResponse(
    val message: String = "validation error",
    val errors: List<ValidationError>,
)

data class ValidationError(
    val field: String,
    val reason: String,
)
