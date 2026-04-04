package com.github.leanite.countries.feature.list.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun `should remove diacritics and lowercase`() {
        assertEquals("austria", "Áustria".normalizedSortKey())
    }

    @Test
    fun `should trim whitespace`() {
        assertEquals("brasil", "  Brasil  ".normalizedSortKey())
    }

    @Test
    fun `should lowercase without diacritics`() {
        assertEquals("argentina", "Argentina".normalizedSortKey())
    }

    @Test
    fun `should handle empty string`() {
        assertEquals("", "".normalizedSortKey())
    }

    @Test
    fun `should handle multiple diacritics`() {
        assertEquals("eire", "Éire".normalizedSortKey())
    }

    @Test
    fun `should handle numbers`() {
        assertEquals("123 land", "123 Land".normalizedSortKey())
    }

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
