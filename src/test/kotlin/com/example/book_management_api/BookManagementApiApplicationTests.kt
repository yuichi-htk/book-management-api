package com.example.book_management_api

import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest(
	properties = [
		"spring.autoconfigure.exclude=" +
			"org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration," +
			"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
			"org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration",
	],
)
class BookManagementApiApplicationTests {

	@MockitoBean
	private lateinit var dsl: DSLContext

	@Test
	fun contextLoads() {
	}

}
