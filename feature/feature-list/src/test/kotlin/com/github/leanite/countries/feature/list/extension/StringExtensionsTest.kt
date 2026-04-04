package com.github.leanite.countries.feature.list.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun `should return uppercase first letter`() {
        assertEquals('A', "Argentina".toSectionLetter())
    }

    @Test
    fun `should normalize diacritics for section letter`() {
        assertEquals('E', "Éire".toSectionLetter())
    }

    @Test
    fun `should return hash for numeric start`() {
        assertEquals('#', "123 Land".toSectionLetter())
    }

    @Test
    fun `should return hash for empty string`() {
        assertEquals('#', "".toSectionLetter())
    }

    @Test
    fun `should return hash for whitespace only`() {
        assertEquals('#', "   ".toSectionLetter())
    }

    @Test
    fun `should trim before resolving letter`() {
        assertEquals('B', "  Brasil".toSectionLetter())
    }

    @Test
    fun `should normalize accented A`() {
        assertEquals('A', "Áustria".toSectionLetter())
    }
}
