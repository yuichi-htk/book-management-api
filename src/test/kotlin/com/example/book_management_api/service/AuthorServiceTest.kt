package com.example.book_management_api.service

import com.example.book_management_api.dto.AuthorRequest
import com.example.book_management_api.exception.ResourceNotFoundException
import com.example.book_management_api.model.PublicationStatus
import com.example.book_management_api.repository.AuthorRepository
import com.example.book_management_api.repository.BookRepository
import com.example.book_management_api.generated.tables.records.AuthorsRecord
import com.example.book_management_api.generated.tables.records.BooksRecord
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class AuthorServiceTest {
    private val authorRepository = mock(AuthorRepository::class.java)
    private val bookRepository = mock(BookRepository::class.java)
    private val service = AuthorService(authorRepository, bookRepository)

    @Test
    fun `createAuthor creates author`() {
        val request = AuthorRequest(name = "Martin Fowler", birthDate = LocalDate.of(1963, 12, 18))
        `when`(authorRepository.insert("Martin Fowler", LocalDate.of(1963, 12, 18)))
            .thenReturn(authorRecord(1L, "Martin Fowler", LocalDate.of(1963, 12, 18)))

        val response = service.createAuthor(request)

        assertEquals(1L, response.id)
        assertEquals("Martin Fowler", response.name)
        assertEquals(LocalDate.of(1963, 12, 18), response.birthDate)
    }

    @Test
    fun `updateAuthor fails when author does not exist`() {
        val request = AuthorRequest(name = "Martin Fowler", birthDate = LocalDate.of(1963, 12, 18))
        `when`(authorRepository.update(999L, "Martin Fowler", LocalDate.of(1963, 12, 18))).thenReturn(null)

        assertFailsWith<ResourceNotFoundException> {
            service.updateAuthor(999L, request)
        }
    }

    @Test
    fun `getBooksByAuthor returns books`() {
        `when`(authorRepository.existsById(1L)).thenReturn(true)
        `when`(bookRepository.findBooksByAuthorId(1L)).thenReturn(
            listOf(
                bookRecord(10L, "Spring Boot入門", 3200, PublicationStatus.PUBLISHED),
                bookRecord(11L, "Kotlin実践", 2800, PublicationStatus.UNPUBLISHED),
            ),
        )

        val response = service.getBooksByAuthor(1L)

        assertEquals(1L, response.authorId)
        assertEquals(listOf(10L, 11L), response.books.map { it.id })
        verify(bookRepository).findBooksByAuthorId(1L)
    }

    @Test
    fun `getBooksByAuthor fails when author does not exist`() {
        `when`(authorRepository.existsById(999L)).thenReturn(false)

        assertFailsWith<ResourceNotFoundException> {
            service.getBooksByAuthor(999L)
        }
    }

    private fun authorRecord(id: Long, name: String, birthDate: LocalDate) = AuthorsRecord(
        id = id,
        name = name,
        birthDate = birthDate,
    )

    private fun bookRecord(id: Long, title: String, price: Int, status: PublicationStatus) = BooksRecord(
        id = id,
        title = title,
        price = price,
        publicationStatus = status.name,
    )
}
