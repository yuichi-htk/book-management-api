package com.example.book_management_api.controller

import com.example.book_management_api.dto.AuthorBooksResponse
import com.example.book_management_api.dto.AuthorRequest
import com.example.book_management_api.dto.AuthorResponse
import com.example.book_management_api.dto.BookSummaryResponse
import com.example.book_management_api.model.PublicationStatus
import com.example.book_management_api.service.AuthorService
import java.time.LocalDate
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AuthorController::class)
class AuthorControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var authorService: AuthorService

    @Test
    fun `POST authors returns created author as json`() {
        `when`(
            authorService.createAuthor(
                AuthorRequest(
                    name = "山田太郎",
                    birthDate = LocalDate.of(1990, 1, 1),
                ),
            ),
        ).thenReturn(
            AuthorResponse(
                id = 1L,
                name = "山田太郎",
                birthDate = LocalDate.of(1990, 1, 1),
            ),
        )

        mockMvc.perform(
            post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"山田太郎","birthDate":"1990-01-01"}"""),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("山田太郎"))
            .andExpect(jsonPath("$.birthDate").value("1990-01-01"))
    }

    @Test
    fun `GET author books returns books as json`() {
        `when`(authorService.getBooksByAuthor(1L)).thenReturn(
            AuthorBooksResponse(
                authorId = 1L,
                books = listOf(
                    BookSummaryResponse(
                        id = 10L,
                        title = "Kotlin入門",
                        price = 3000,
                        publicationStatus = PublicationStatus.UNPUBLISHED,
                    ),
                ),
            ),
        )

        mockMvc.perform(get("/authors/1/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.authorId").value(1))
            .andExpect(jsonPath("$.books", hasSize<Any>(1)))
            .andExpect(jsonPath("$.books[0].id").value(10))
            .andExpect(jsonPath("$.books[0].title").value("Kotlin入門"))
            .andExpect(jsonPath("$.books[0].price").value(3000))
            .andExpect(jsonPath("$.books[0].publicationStatus").value("UNPUBLISHED"))
    }
}
