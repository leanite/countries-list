package com.github.leanite.countries.feature.list.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateCountryTargetZoomUseCaseTest {

    private val useCase = CalculateCountryTargetZoomUseCase()

    @Test
    fun `should return default zoom when area is null`() {
        val result = useCase(null)

        assertEquals(5.8f, result, 0.01f)
    }

    @Test
    fun `should return default zoom when area is zero`() {
        val result = useCase(0.0)

        assertEquals(5.8f, result, 0.01f)
    }

    @Test
    fun `should return default zoom when area is negative`() {
        val result = useCase(-100.0)

        assertEquals(5.8f, result, 0.01f)
    }

    @Test
    fun `should return higher zoom for small countries`() {
        val result = useCase(468.0) // Luxembourg-sized

        assertEquals(7.2f, result, 0.01f) // clamps to max
    }

    @Test
    fun `should return lower zoom for large countries`() {
        val result = useCase(8_515_767.0) // Brazil

        assertEquals(3.89f, result, 0.1f)
    }

    @Test
    fun `should not exceed max zoom`() {
        val result = useCase(1.0) // tiny area

        assertEquals(7.2f, result, 0.01f)
    }

    @Test
    fun `should not go below min zoom`() {
        val result = useCase(1_000_000_000.0) // absurdly large

        assertEquals(3.2f, result, 0.01f)
    }

    @Test
    fun `should return intermediate zoom for medium countries`() {
        val resultPortugal = useCase(92_212.0) // reference area

        assertEquals(5.9f, resultPortugal, 0.1f)
    }
}
