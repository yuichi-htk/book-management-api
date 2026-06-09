package com.example.book_management_api.controller

import com.example.book_management_api.dto.AuthorRequest
import com.example.book_management_api.dto.AuthorResponse
import com.example.book_management_api.service.AuthorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authors")
class AuthorController(
    private val authorService: AuthorService,
) {
    @PostMapping
    fun createAuthor(@Valid @RequestBody request: AuthorRequest): ResponseEntity<AuthorResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(request))

    @PutMapping("/{authorId}")
    fun updateAuthor(
        @PathVariable authorId: Long,
        @Valid @RequestBody request: AuthorRequest,
    ): ResponseEntity<AuthorResponse> =
        ResponseEntity.ok(authorService.updateAuthor(authorId, request))
}
