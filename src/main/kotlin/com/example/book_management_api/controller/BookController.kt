package com.example.book_management_api.controller

import com.example.book_management_api.dto.BookRequest
import com.example.book_management_api.dto.BookResponse
import com.example.book_management_api.service.BookService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(
    private val bookService: BookService,
) {
    @PostMapping
    fun createBook(@Valid @RequestBody request: BookRequest): ResponseEntity<BookResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request))

    @PutMapping("/{bookId}")
    fun updateBook(
        @PathVariable bookId: Long,
        @Valid @RequestBody request: BookRequest,
    ): ResponseEntity<BookResponse> =
        ResponseEntity.ok(bookService.updateBook(bookId, request))
}
