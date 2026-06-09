package com.example.book_management_api.dto

data class AuthorBooksResponse(
    val authorId: Long,
    val books: List<BookSummaryResponse>,
)
