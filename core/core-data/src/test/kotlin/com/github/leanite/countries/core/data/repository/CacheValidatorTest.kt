package com.github.leanite.countries.core.data.repository

import com.github.leanite.countries.core.data.datasource.CountryLocalDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class CacheValidatorTest {

    @MockK
    lateinit var localDataSource: CountryLocalDataSource

    private val cacheTtlMillis = 120_000L // 2 minutes
    private var currentTime = 1_000_000L

    private lateinit var validator: CacheValidator

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        validator = CacheValidator(
            localDataSource = localDataSource,
            cacheTtlMillis = cacheTtlMillis,
            clock = { currentTime }
        )
    }

    @Test
    fun `all countries cache should be valid when within TTL`() = runTest {
        coEvery { localDataSource.getAllCountriesLastUpdatedAtMillis() } returns 950_000L

        assertTrue(validator.isAllCountriesValid())
    }

    @Test
    fun `all countries cache should be invalid when expired`() = runTest {
        coEvery { localDataSource.getAllCountriesLastUpdatedAtMillis() } returns 800_000L

        assertFalse(validator.isAllCountriesValid())
    }

    @Test
    fun `all countries cache should be invalid when no metadata`() = runTest {
        coEvery { localDataSource.getAllCountriesLastUpdatedAtMillis() } returns null

        assertFalse(validator.isAllCountriesValid())
    }

    @Test
    fun `all countries cache should be invalid at exact TTL boundary`() = runTest {
        coEvery { localDataSource.getAllCountriesLastUpdatedAtMillis() } returns (currentTime - cacheTtlMillis)

        assertFalse(validator.isAllCountriesValid())
    }

    @Test
    fun `all countries cache should be valid one ms before TTL`() = runTest {
        coEvery { localDataSource.getAllCountriesLastUpdatedAtMillis() } returns (currentTime - cacheTtlMillis + 1)

        assertTrue(validator.isAllCountriesValid())
    }

    @Test
    fun `country cache should be valid when within TTL`() = runTest {
        coEvery { localDataSource.getCountryLastUpdatedAtMillis("BRA") } returns 950_000L

        assertTrue(validator.isCountryValid("BRA"))
    }

    @Test
    fun `country cache should be invalid when expired`() = runTest {
        coEvery { localDataSource.getCountryLastUpdatedAtMillis("BRA") } returns 800_000L

        assertFalse(validator.isCountryValid("BRA"))
    }

    @Test
    fun `country cache should be invalid when no metadata`() = runTest {
        coEvery { localDataSource.getCountryLastUpdatedAtMillis("BRA") } returns null

        assertFalse(validator.isCountryValid("BRA"))
    }

    @Test
    fun `details cache should be valid when within TTL`() = runTest {
        coEvery { localDataSource.getCountryDetailsLastUpdatedAtMillis("BRA") } returns 950_000L

        assertTrue(validator.isDetailsValid("BRA"))
    }

    @Test
    fun `details cache should be invalid when expired`() = runTest {
        coEvery { localDataSource.getCountryDetailsLastUpdatedAtMillis("BRA") } returns 800_000L

        assertFalse(validator.isDetailsValid("BRA"))
    }

    @Test
    fun `details cache should be invalid when no metadata`() = runTest {
        coEvery { localDataSource.getCountryDetailsLastUpdatedAtMillis("BRA") } returns null

        assertFalse(validator.isDetailsValid("BRA"))
    }

}
