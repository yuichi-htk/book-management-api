package com.example.book_management_api.dto

import com.example.book_management_api.model.PublicationStatus

data class BookResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val publicationStatus: PublicationStatus,
    val authors: List<AuthorSummaryResponse>,
)
