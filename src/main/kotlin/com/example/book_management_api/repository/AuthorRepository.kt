package com.example.book_management_api.repository

import com.example.bookmanagementapi.generated.tables.records.AuthorsRecord
import com.example.bookmanagementapi.generated.tables.references.AUTHORS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class AuthorRepository(
    private val dsl: DSLContext,
) {
    fun insert(name: String, birthDate: LocalDate): AuthorsRecord =
        dsl.insertInto(AUTHORS)
            .set(AUTHORS.NAME, name)
            .set(AUTHORS.BIRTH_DATE, birthDate)
            .returning(AUTHORS.ID, AUTHORS.NAME, AUTHORS.BIRTH_DATE)
            .fetchOne()
            ?: throw IllegalStateException("Failed to insert author.")

    fun update(id: Long, name: String, birthDate: LocalDate): AuthorsRecord? =
        dsl.update(AUTHORS)
            .set(AUTHORS.NAME, name)
            .set(AUTHORS.BIRTH_DATE, birthDate)
            .set(AUTHORS.UPDATED_AT, LocalDateTime.now())
            .where(AUTHORS.ID.eq(id))
            .returning(AUTHORS.ID, AUTHORS.NAME, AUTHORS.BIRTH_DATE)
            .fetchOne()

    fun findByIds(ids: Collection<Long>): List<AuthorsRecord> =
        dsl.selectFrom(AUTHORS)
            .where(AUTHORS.ID.`in`(ids))
            .fetch()
}
