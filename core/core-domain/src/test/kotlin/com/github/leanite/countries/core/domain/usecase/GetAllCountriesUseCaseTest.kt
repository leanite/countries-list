package com.github.leanite.countries.core.domain.usecase

import com.github.leanite.countries.core.domain.result.AppError
import com.github.leanite.countries.core.domain.repository.CountryRepository
import com.github.leanite.countries.core.domain.result.AppResult
import com.github.leanite.countries.core.domain.fixture.CountryFixtures.countriesNotTrimmed
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetAllCountriesUseCaseTest {

    @MockK
    lateinit var repository: CountryRepository

    @InjectMockKs
    lateinit var useCase: GetAllCountriesUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should trim, filter and sort countries`() = runTest {
        coEvery { repository.getCountries() } returns AppResult.Success(countriesNotTrimmed)

        val result = useCase()

        assertTrue(result is AppResult.Success)
        val data = (result as AppResult.Success).data
        assertEquals(listOf("Argentina", "Áustria", "Brasil"), data.map { it.name })
    }

    @Test
    fun `should propagate repository error`() = runTest {
        coEvery { repository.getCountries() } returns AppResult.Error(AppError.NoInternet)

        val result = useCase()

        assertTrue(result is AppResult.Error)
        assertEquals(AppError.NoInternet, (result as AppResult.Error).error)
    }
}
