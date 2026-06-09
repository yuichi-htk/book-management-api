package com.example.book_management_api.service

import com.example.book_management_api.dto.BookRequest
import com.example.book_management_api.exception.BadRequestException
import com.example.book_management_api.exception.BusinessRuleViolationException
import com.example.book_management_api.exception.ResourceNotFoundException
import com.example.book_management_api.model.PublicationStatus
import com.example.book_management_api.repository.AuthorRepository
import com.example.book_management_api.repository.BookRepository
import com.example.bookmanagementapi.generated.tables.records.AuthorsRecord
import com.example.bookmanagementapi.generated.tables.records.BooksRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class BookServiceTest {
    private val bookRepository = mock(BookRepository::class.java)
    private val authorRepository = mock(AuthorRepository::class.java)
    private val service = BookService(bookRepository, authorRepository)

    @Test
    fun `createBook creates book with authors`() {
        val request = bookRequest(authorIds = listOf(1L, 2L))
        `when`(authorRepository.findByIds(listOf(1L, 2L))).thenReturn(
            listOf(authorRecord(1L, "Eric Evans"), authorRecord(2L, "Martin Fowler")),
        )
        `when`(bookRepository.insertBook("Domain-Driven Design", 4200, PublicationStatus.UNPUBLISHED))
            .thenReturn(bookRecord(10L, "Domain-Driven Design", 4200, PublicationStatus.UNPUBLISHED))

        val response = service.createBook(request)

        assertEquals(10L, response.id)
        assertEquals("Domain-Driven Design", response.title)
        assertEquals(2, response.authors.size)
        assertEquals(listOf(1L, 2L), response.authors.map { it.id })
        verify(bookRepository).insertBookAuthors(10L, listOf(1L, 2L))
    }

    @Test
    fun `createBook fails when authorIds is empty`() {
        val request = bookRequest(authorIds = emptyList())

        assertFailsWith<BadRequestException> {
            service.createBook(request)
        }
        verify(bookRepository, never()).insertBook("Domain-Driven Design", 4200, PublicationStatus.UNPUBLISHED)
    }

    @Test
    fun `createBook fails when authorIds contains duplicates`() {
        val request = bookRequest(authorIds = listOf(1L, 1L))

        assertFailsWith<BadRequestException> {
            service.createBook(request)
        }
        verify(bookRepository, never()).insertBook("Domain-Driven Design", 4200, PublicationStatus.UNPUBLISHED)
    }

    @Test
    fun `createBook fails when author does not exist`() {
        val request = bookRequest(authorIds = listOf(1L, 999L))
        `when`(authorRepository.findByIds(listOf(1L, 999L))).thenReturn(listOf(authorRecord(1L, "Eric Evans")))

        assertFailsWith<BadRequestException> {
            service.createBook(request)
        }
        verify(bookRepository, never()).insertBook("Domain-Driven Design", 4200, PublicationStatus.UNPUBLISHED)
    }

    @Test
    fun `updateBook replaces authors and returns updated book`() {
        val request = bookRequest(
            title = "Refactoring",
            price = 5000,
            publicationStatus = PublicationStatus.PUBLISHED,
            authorIds = listOf(2L, 3L),
        )
        `when`(bookRepository.findById(10L))
            .thenReturn(bookRecord(10L, "Old Title", 4200, PublicationStatus.UNPUBLISHED))
        `when`(authorRepository.findByIds(listOf(2L, 3L))).thenReturn(
            listOf(authorRecord(2L, "Martin Fowler"), authorRecord(3L, "Kent Beck")),
        )
        `when`(bookRepository.updateBook(10L, "Refactoring", 5000, PublicationStatus.PUBLISHED))
            .thenReturn(bookRecord(10L, "Refactoring", 5000, PublicationStatus.PUBLISHED))

        val response = service.updateBook(10L, request)

        assertEquals("Refactoring", response.title)
        assertEquals(PublicationStatus.PUBLISHED, response.publicationStatus)
        assertEquals(listOf(2L, 3L), response.authors.map { it.id })
        verify(bookRepository).replaceAuthors(10L, listOf(2L, 3L))
    }

    @Test
    fun `updateBook fails when book does not exist`() {
        `when`(bookRepository.findById(999L)).thenReturn(null)

        assertFailsWith<ResourceNotFoundException> {
            service.updateBook(999L, bookRequest())
        }
    }

    @Test
    fun `updateBook fails when published book is changed to unpublished`() {
        `when`(bookRepository.findById(10L))
            .thenReturn(bookRecord(10L, "Domain-Driven Design", 4200, PublicationStatus.PUBLISHED))

        assertFailsWith<BusinessRuleViolationException> {
            service.updateBook(10L, bookRequest(publicationStatus = PublicationStatus.UNPUBLISHED))
        }
        verify(bookRepository, never()).updateBook(10L, "Domain-Driven Design", 4200, PublicationStatus.UNPUBLISHED)
    }

    private fun bookRequest(
        title: String = "Domain-Driven Design",
        price: Int = 4200,
        publicationStatus: PublicationStatus = PublicationStatus.UNPUBLISHED,
        authorIds: List<Long> = listOf(1L),
    ) = BookRequest(
        title = title,
        price = price,
        authorIds = authorIds,
        publicationStatus = publicationStatus,
    )

    private fun authorRecord(id: Long, name: String) = AuthorsRecord(
        id = id,
        name = name,
        birthDate = java.time.LocalDate.of(1960, 1, 1),
    )

    private fun bookRecord(id: Long, title: String, price: Int, status: PublicationStatus) = BooksRecord(
        id = id,
        title = title,
        price = price,
        publicationStatus = status.name,
    )
}
