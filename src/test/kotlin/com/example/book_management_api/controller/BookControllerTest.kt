package com.example.book_management_api.controller

import com.example.book_management_api.dto.AuthorSummaryResponse
import com.example.book_management_api.dto.BookRequest
import com.example.book_management_api.dto.BookResponse
import com.example.book_management_api.model.PublicationStatus
import com.example.book_management_api.service.BookService
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(BookController::class)
class BookControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var bookService: BookService

    @Test
    fun `POST books returns created book as json`() {
        `when`(
            bookService.createBook(
                BookRequest(
                    title = "Kotlin入門",
                    price = 3000,
                    authorIds = listOf(1L),
                    publicationStatus = PublicationStatus.UNPUBLISHED,
                ),
            ),
        ).thenReturn(
            BookResponse(
                id = 10L,
                title = "Kotlin入門",
                price = 3000,
                publicationStatus = PublicationStatus.UNPUBLISHED,
                authors = listOf(AuthorSummaryResponse(id = 1L, name = "山田太郎")),
            ),
        )

        mockMvc.perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "Kotlin入門",
                      "price": 3000,
                      "authorIds": [1],
                      "publicationStatus": "UNPUBLISHED"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.title").value("Kotlin入門"))
            .andExpect(jsonPath("$.price").value(3000))
            .andExpect(jsonPath("$.publicationStatus").value("UNPUBLISHED"))
            .andExpect(jsonPath("$.authors", hasSize<Any>(1)))
            .andExpect(jsonPath("$.authors[0].id").value(1))
            .andExpect(jsonPath("$.authors[0].name").value("山田太郎"))
    }
}
