package com.example.book_management_api.service

import com.example.book_management_api.dto.AuthorRequest
import com.example.book_management_api.dto.AuthorResponse
import com.example.book_management_api.exception.ResourceNotFoundException
import com.example.book_management_api.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
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
}

private fun com.example.bookmanagementapi.generated.tables.records.AuthorsRecord.toResponse(): AuthorResponse =
    AuthorResponse(
        id = requireNotNull(id),
        name = requireNotNull(name),
        birthDate = requireNotNull(birthDate),
    )
