package com.example.book_management_api.dto

import com.example.book_management_api.model.PublicationStatus
import jakarta.validation.Validation
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertTrue

class RequestValidationTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `AuthorRequest rejects future birthDate`() {
        val request = AuthorRequest(
            name = "Martin Fowler",
            birthDate = LocalDate.now().plusDays(1),
        )

        val violations = validator.validate(request)

        assertTrue(violations.any { it.propertyPath.toString() == "birthDate" })
    }

    @Test
    fun `AuthorRequest rejects blank name`() {
        val request = AuthorRequest(
            name = "   ",
            birthDate = LocalDate.of(1963, 12, 18),
        )

        val violations = validator.validate(request)

        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `BookRequest rejects negative price`() {
        val request = bookRequest(price = -1)

        val violations = validator.validate(request)

        assertTrue(violations.any { it.propertyPath.toString() == "price" })
    }

    @Test
    fun `BookRequest rejects empty authorIds`() {
        val request = bookRequest(authorIds = emptyList())

        val violations = validator.validate(request)

        assertTrue(violations.any { it.propertyPath.toString() == "authorIds" })
    }

    @Test
    fun `BookRequest rejects duplicated authorIds`() {
        val request = bookRequest(authorIds = listOf(1L, 1L))

        val violations = validator.validate(request)

        assertTrue(violations.any { it.propertyPath.toString() == "authorIds" })
    }

    private fun bookRequest(
        title: String = "Domain-Driven Design",
        price: Int = 4200,
        authorIds: List<Long> = listOf(1L),
        publicationStatus: PublicationStatus = PublicationStatus.UNPUBLISHED,
    ) = BookRequest(
        title = title,
        price = price,
        authorIds = authorIds,
        publicationStatus = publicationStatus,
    )
}
