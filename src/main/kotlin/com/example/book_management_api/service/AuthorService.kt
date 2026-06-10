package com.example.book_management_api.service

import com.example.book_management_api.dto.AuthorBooksResponse
import com.example.book_management_api.dto.AuthorRequest
import com.example.book_management_api.dto.AuthorResponse
import com.example.book_management_api.dto.BookSummaryResponse
import com.example.book_management_api.exception.ResourceNotFoundException
import com.example.book_management_api.model.PublicationStatus
import com.example.book_management_api.repository.AuthorRepository
import com.example.book_management_api.repository.BookRepository
import com.example.book_management_api.generated.tables.records.BooksRecord
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository,
) {
    @Transactional
    fun createAuthor(request: AuthorRequest): AuthorResponse {
        val record = authorRepository.insert(
            name = requireNotNull(request.name),
            birthDate = requireNotNull(request.birthDate),
        )

        return record.toResponse()
    }

    @Transactional
    fun updateAuthor(authorId: Long, request: AuthorRequest): AuthorResponse {
        val record = authorRepository.update(
            id = authorId,
            name = requireNotNull(request.name),
            birthDate = requireNotNull(request.birthDate),
        ) ?: throw ResourceNotFoundException("Author not found.")

        return record.toResponse()
    }

    @Transactional(readOnly = true)
    fun getBooksByAuthor(authorId: Long): AuthorBooksResponse {
        if (!authorRepository.existsById(authorId)) {
            throw ResourceNotFoundException("Author not found.")
        }

        return AuthorBooksResponse(
            authorId = authorId,
            books = bookRepository.findBooksByAuthorId(authorId).map { it.toSummaryResponse() },
        )
    }
}

private fun com.example.book_management_api.generated.tables.records.AuthorsRecord.toResponse(): AuthorResponse =
    AuthorResponse(
        id = requireNotNull(id),
        name = requireNotNull(name),
        birthDate = requireNotNull(birthDate),
    )

private fun BooksRecord.toSummaryResponse(): BookSummaryResponse =
    BookSummaryResponse(
        id = requireNotNull(id),
        title = requireNotNull(title),
        price = requireNotNull(price),
        publicationStatus = PublicationStatus.valueOf(requireNotNull(publicationStatus)),
    )
