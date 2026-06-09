package com.example.book_management_api.exception

class BadRequestException(
    override val message: String,
    val code: String = "BAD_REQUEST",
) : RuntimeException(message)
