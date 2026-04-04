package com.github.leanite.countries.feature.details.usecase

import com.github.leanite.countries.core.domain.error.AppError
import com.github.leanite.countries.core.domain.repository.CountryRepository
import com.github.leanite.countries.core.domain.result.AppResult
import com.github.leanite.countries.feature.details.fixtures.brazilCountryDetails
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetCountryDetailsUseCaseTest {
    @MockK
    lateinit var repository: CountryRepository
    @InjectMockKs
    lateinit var useCase: GetCountryDetailsUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should return country details from repository`() = runTest {
        val details = brazilCountryDetails
        coEvery { repository.getCountryDetails("BRA") } returns AppResult.Success(details)

        val result = useCase("BRA")

        Assert.assertTrue(result is AppResult.Success)
        Assert.assertEquals("Brasil", (result as AppResult.Success).data.name)
    }

    @Test
    fun `should propagate repository error`() = runTest {
        coEvery { repository.getCountryDetails("BRA") } returns AppResult.Error(AppError.NoInternet)

        val result = useCase("BRA")

        Assert.assertTrue(result is AppResult.Error)
        Assert.assertEquals(AppError.NoInternet, (result as AppResult.Error).error)
    }
}