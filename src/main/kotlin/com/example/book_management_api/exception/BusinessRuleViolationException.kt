package com.example.book_management_api.exception

class BusinessRuleViolationException(
    override val message: String,
    val code: String = "BUSINESS_RULE_VIOLATION",
) : RuntimeException(message)
