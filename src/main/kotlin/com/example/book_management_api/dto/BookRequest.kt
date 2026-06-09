package com.example.book_management_api.dto

import com.example.book_management_api.model.PublicationStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.UniqueElements

data class BookRequest(
    @field:NotBlank
    val title: String?,

    @field:NotNull
    @field:Min(0)
    val price: Int?,

    @field:NotEmpty
    @field:UniqueElements
    val authorIds: List<Long>?,

    @field:NotNull
    val publicationStatus: PublicationStatus?,
)
