package com.example.book_management_api.exception

class ResourceNotFoundException(
    override val message: String,
    val code: String = "RESOURCE_NOT_FOUND",
) : RuntimeException(message)
