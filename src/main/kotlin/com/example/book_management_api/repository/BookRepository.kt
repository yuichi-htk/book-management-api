package com.example.book_management_api.repository

import com.example.book_management_api.model.PublicationStatus
import com.example.bookmanagementapi.generated.tables.records.BooksRecord
import com.example.bookmanagementapi.generated.tables.references.BOOKS
import com.example.bookmanagementapi.generated.tables.references.BOOK_AUTHORS
import org.jooq.DSLContext
import org.jooq.impl.DSL.exists
import org.jooq.impl.DSL.selectOne
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class BookRepository(
    private val dsl: DSLContext,
) {
    fun insertBook(title: String, price: Int, publicationStatus: PublicationStatus): BooksRecord =
        dsl.insertInto(BOOKS)
            .set(BOOKS.TITLE, title)
            .set(BOOKS.PRICE, price)
            .set(BOOKS.PUBLICATION_STATUS, publicationStatus.name)
            .returning(BOOKS.ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.PUBLICATION_STATUS)
            .fetchOne()
            ?: throw IllegalStateException("Failed to insert book.")

    fun insertBookAuthors(bookId: Long, authorIds: List<Long>) {
        val queries = authorIds.map { authorId ->
            dsl.insertInto(BOOK_AUTHORS)
                .set(BOOK_AUTHORS.BOOK_ID, bookId)
                .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
        }

        dsl.batch(queries).execute()
    }

    fun replaceAuthors(bookId: Long, authorIds: List<Long>) {
        deleteBookAuthors(bookId)
        insertBookAuthors(bookId = bookId, authorIds = authorIds)
    }

    fun deleteBookAuthors(bookId: Long) {
        dsl.deleteFrom(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(bookId))
            .execute()
    }

    fun updateBook(id: Long, title: String, price: Int, publicationStatus: PublicationStatus): BooksRecord? =
        dsl.update(BOOKS)
            .set(BOOKS.TITLE, title)
            .set(BOOKS.PRICE, price)
            .set(BOOKS.PUBLICATION_STATUS, publicationStatus.name)
            .set(BOOKS.UPDATED_AT, LocalDateTime.now())
            .where(BOOKS.ID.eq(id))
            .returning(BOOKS.ID, BOOKS.TITLE, BOOKS.PRICE, BOOKS.PUBLICATION_STATUS)
            .fetchOne()

    fun findById(id: Long): BooksRecord? =
        dsl.selectFrom(BOOKS)
            .where(BOOKS.ID.eq(id))
            .fetchOne()

    fun existsById(id: Long): Boolean =
        dsl.fetchExists(
            selectOne()
                .from(BOOKS)
                .where(BOOKS.ID.eq(id)),
        )

    fun findBooksByAuthorId(authorId: Long): List<BooksRecord> =
        dsl.select(BOOKS.fields().toList())
            .from(BOOKS)
            .join(BOOK_AUTHORS).on(BOOK_AUTHORS.BOOK_ID.eq(BOOKS.ID))
            .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
            .orderBy(BOOKS.ID.asc())
            .fetchInto(BooksRecord::class.java)
}
