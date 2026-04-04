package com.github.leanite.countries.core.domain.usecase

import com.github.leanite.countries.core.domain.result.AppError
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.repository.CountryRepository
import com.github.leanite.countries.core.domain.result.AppResult
import com.github.leanite.countries.core.domain.fixture.CountryFixtures.argentinaCountry
import com.github.leanite.countries.core.domain.fixture.CountryFixtures.brazilCountry
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetBorderCountriesUseCaseTest {
    @MockK
    lateinit var repository: CountryRepository
    @InjectMockKs
    lateinit var useCase: GetBorderCountriesUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should return empty list when codes list is empty`() = runTest {
        val result = useCase(emptyList())

        assertTrue(result is AppResult.Success)
        assertEquals(emptyList<Country>(), (result as AppResult.Success).data)
        coVerify(exactly = 0) { repository.getCountry(any()) }
    }

    @Test
    fun `should fetch only distinct border codes and return countries`() = runTest {
        coEvery { repository.getCountry("BRA") } returns AppResult.Success(brazilCountry)
        coEvery { repository.getCountry("ARG") } returns AppResult.Success(argentinaCountry)

        val result = useCase(listOf("BRA", "BRA", "ARG"))
        assertTrue(result is AppResult.Success)

        val data = (result as AppResult.Success).data
        assertEquals(2, data.size)
        assertEquals(setOf("BRA", "ARG"), data.map { it.code?.value }.toSet())

        coVerify(exactly = 1) { repository.getCountry("BRA") }
        coVerify(exactly = 1) { repository.getCountry("ARG") }
    }

    @Test
    fun `should return error when any border country request fails`() = runTest {
        coEvery { repository.getCountry("BRA") } returns AppResult.Success(brazilCountry)
        coEvery { repository.getCountry("ARG") } returns AppResult.Error(AppError.NotFound)

        val result = useCase(listOf("BRA", "ARG"))

        assertTrue(result is AppResult.Error)
        assertEquals(AppError.NotFound, (result as AppResult.Error).error)
    }
}
