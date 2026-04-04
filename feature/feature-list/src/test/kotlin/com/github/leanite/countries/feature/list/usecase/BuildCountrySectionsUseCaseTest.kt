package com.github.leanite.countries.feature.list.usecase

import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.feature.list.fixture.CountryFixtures.countriesMixed
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BuildCountrySectionsUseCaseTest {

    @InjectMockKs
    lateinit var useCase: BuildCountrySectionsUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should build sections and side index`() {
        val result = useCase(countriesMixed)

        assertEquals(listOf('A', 'B', 'C', 'E', '#'), result.sections.map { it.letter })
        assertEquals(listOf('A', 'B', 'C', 'E', '#'), result.index.map { it.letter })
        assertEquals(listOf(0, 3, 5, 7, 9), result.index.map { it.firstItemPosition })

        val aSection = result.sections.first { it.letter == 'A' }
        assertEquals(listOf("Argentina", "Áustria"), aSection.countries.map { it.name })
    }

    @Test
    fun `should return empty sections and index when input is empty`() {
        val result = useCase(emptyList())

        assertTrue(result.sections.isEmpty())
        assertTrue(result.index.isEmpty())
    }
}
