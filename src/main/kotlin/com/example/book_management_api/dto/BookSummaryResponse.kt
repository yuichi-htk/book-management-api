package com.example.book_management_api.dto

import com.example.book_management_api.model.PublicationStatus

data class BookSummaryResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val publicationStatus: PublicationStatus,
)
