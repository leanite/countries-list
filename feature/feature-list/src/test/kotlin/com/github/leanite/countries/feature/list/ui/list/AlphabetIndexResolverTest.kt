package com.github.leanite.countries.feature.list.ui.list

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlphabetIndexResolverTest {

    private val resolver = AlphabetIndexResolver()

    private val indexByLetter = mapOf(
        'A' to 0,
        'B' to 5,
        'M' to 20,
        'Z' to 100
    )

    @Test
    fun `should resolve first letter when Y is at top`() {
        val result = resolver.resolveLetterFromY(
            y = 0f,
            containerHeightPx = 1000f,
            indexByLetter = indexByLetter
        )

        assertEquals('A', result)
    }

    @Test
    fun `should resolve last available letter when Y is at bottom`() {
        val result = resolver.resolveLetterFromY(
            y = 999f,
            containerHeightPx = 1000f,
            indexByLetter = indexByLetter
        )

        assertEquals('Z', result)
    }

    @Test
    fun `should resolve nearest available letter when exact letter is not in index`() {
        val result = resolver.resolveLetterFromY(
            y = 100f,
            containerHeightPx = 1000f,
            indexByLetter = indexByLetter
        )

        assertEquals('B', result)
    }

    @Test
    fun `should clamp negative Y to first position`() {
        val result = resolver.resolveLetterFromY(
            y = -50f,
            containerHeightPx = 1000f,
            indexByLetter = indexByLetter
        )

        assertEquals('A', result)
    }

    @Test
    fun `should clamp Y beyond container to last position`() {
        val result = resolver.resolveLetterFromY(
            y = 2000f,
            containerHeightPx = 1000f,
            indexByLetter = indexByLetter
        )

        assertEquals('Z', result)
    }

    @Test
    fun `should return null when index is empty`() {
        val result = resolver.resolveLetterFromY(
            y = 500f,
            containerHeightPx = 1000f,
            indexByLetter = emptyMap()
        )

        assertNull(result)
    }

    @Test
    fun `should return exact letter when available`() {
        val aIndex = resolver.letters.indexOf('A')

        val result = resolver.findNearestAvailableLetter(aIndex, indexByLetter)

        assertEquals('A', result)
    }

    @Test
    fun `should find nearest letter above when exact is unavailable`() {
        val cIndex = resolver.letters.indexOf('C')

        val result = resolver.findNearestAvailableLetter(cIndex, indexByLetter)

        assertEquals('B', result)
    }

    @Test
    fun `should find nearest letter below when closer`() {
        val lIndex = resolver.letters.indexOf('L')

        val result = resolver.findNearestAvailableLetter(lIndex, indexByLetter)

        assertEquals('M', result)
    }

    @Test
    fun `should return null when no letters are available`() {
        val result = resolver.findNearestAvailableLetter(5, emptyMap())

        assertNull(result)
    }

    @Test
    fun `should find hash when only hash is available`() {
        val hashIndex = resolver.letters.indexOf('#')

        val result = resolver.findNearestAvailableLetter(hashIndex, mapOf('#' to 0))

        assertEquals('#', result)
    }

    @Test
    fun `should handle start index at beginning`() {
        val result = resolver.findNearestAvailableLetter(0, mapOf('Z' to 0))

        assertEquals('Z', result)
    }

    @Test
    fun `should handle start index at end`() {
        val result = resolver.findNearestAvailableLetter(
            resolver.letters.lastIndex,
            mapOf('A' to 0)
        )

        assertEquals('A', result)
    }

}
