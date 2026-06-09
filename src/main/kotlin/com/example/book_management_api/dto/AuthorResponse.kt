package com.example.book_management_api.dto

import java.time.LocalDate

data class AuthorResponse(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
)
