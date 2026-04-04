package com.github.leanite.countries.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CountryCodeTest {

    @Test
    fun `should store value as-is`() {
        val code = CountryCode("BRA")
        assertEquals("BRA", code.value)
    }

    @Test
    fun `should be equal when same value`() {
        val code1 = CountryCode("BRA")
        val code2 = CountryCode("BRA")
        assertEquals(code1, code2)
    }

    @Test
    fun `should not be equal when different value`() {
        val code1 = CountryCode("BRA")
        val code2 = CountryCode("ARG")
        assertNotEquals(code1, code2)
    }

    @Test
    fun `should preserve whitespace in value`() {
        val code = CountryCode(" BRA ")
        assertEquals(" BRA ", code.value)
    }
}
