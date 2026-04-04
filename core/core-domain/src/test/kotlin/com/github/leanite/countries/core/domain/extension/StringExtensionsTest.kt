package com.github.leanite.countries.core.domain.extension

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
}
