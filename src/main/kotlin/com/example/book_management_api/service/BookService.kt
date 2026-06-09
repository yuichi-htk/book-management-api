package com.example.book_management_api.service

import com.example.book_management_api.dto.AuthorSummaryResponse
import com.example.book_management_api.dto.BookRequest
import com.example.book_management_api.dto.BookResponse
import com.example.book_management_api.exception.BadRequestException
import com.example.book_management_api.repository.AuthorRepository
import com.example.book_management_api.repository.BookRepository
import com.example.bookmanagementapi.generated.tables.records.AuthorsRecord
import com.example.bookmanagementapi.generated.tables.records.BooksRecord
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
) {
    @Transactional
    fun createBook(request: BookRequest): BookResponse {
        val authorIds = requireNotNull(request.authorIds)
        validateAuthorIds(authorIds)

        val authors = authorRepository.findByIds(authorIds)
        if (authors.size != authorIds.toSet().size) {
            throw BadRequestException("Author not found.")
        }

        val book = bookRepository.insertBook(
            title = requireNotNull(request.title),
            price = requireNotNull(request.price),
            publicationStatus = requireNotNull(request.publicationStatus),
        )
        val bookId = requireNotNull(book.id)

        bookRepository.insertBookAuthors(bookId = bookId, authorIds = authorIds)

        return book.toResponse(authorIds.toAuthorSummaries(authors))
    }

    private fun validateAuthorIds(authorIds: List<Long>) {
        if (authorIds.isEmpty()) {
            throw BadRequestException("Book must have at least one author.")
        }
        if (authorIds.size != authorIds.toSet().size) {
            throw BadRequestException("authorIds must not contain duplicates.")
        }
    }
}

private fun BooksRecord.toResponse(authors: List<AuthorSummaryResponse>): BookResponse =
    BookResponse(
        id = requireNotNull(id),
        title = requireNotNull(title),
        price = requireNotNull(price),
        publicationStatus = com.example.book_management_api.model.PublicationStatus.valueOf(
            requireNotNull(publicationStatus),
        ),
        authors = authors,
    )

private fun List<Long>.toAuthorSummaries(authors: List<AuthorsRecord>): List<AuthorSummaryResponse> {
    val authorsById = authors.associateBy { requireNotNull(it.id) }

    return map { authorId ->
        val author = requireNotNull(authorsById[authorId])
        AuthorSummaryResponse(
            id = requireNotNull(author.id),
            name = requireNotNull(author.name),
        )
    }
}
