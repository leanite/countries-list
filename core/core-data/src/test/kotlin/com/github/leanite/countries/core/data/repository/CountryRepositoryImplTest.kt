package com.github.leanite.countries.core.data.repository

import com.github.leanite.countries.core.data.datasource.CountryLocalDataSource
import com.github.leanite.countries.core.data.datasource.CountryRemoteDataSource
import com.github.leanite.countries.core.data.fixture.CountryDTOFixtures.remoteBrazilDto
import com.github.leanite.countries.core.data.fixture.CountryDTOFixtures.remoteListWithNull
import com.github.leanite.countries.core.data.fixture.CountryDTOFixtures.remoteNullDto
import com.github.leanite.countries.core.data.fixture.CountryEntityFixtures.fakeBordersCountries
import com.github.leanite.countries.core.data.fixture.CountryEntityFixtures.localBrazilEntity
import com.github.leanite.countries.core.data.fixture.brazilCountryDetails
import com.github.leanite.countries.core.data.fixture.remoteBrazilDetailsDto
import com.github.leanite.countries.core.domain.error.AppError
import com.github.leanite.countries.core.domain.result.AppResult
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.InjectionLookupType
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


internal class CountryRepositoryImplTest {
    @MockK
    lateinit var remoteDataSource: CountryRemoteDataSource
    @MockK
    lateinit var localDataSource: CountryLocalDataSource
    private val cacheTtlMillis: Long = 2 * 60 * 1000L
    @InjectMockKs(lookupType = InjectionLookupType.BY_NAME)
    lateinit var repository: CountryRepositoryImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should return local data when all countries cache is valid and local is not empty`() = runTest {
        val now = System.currentTimeMillis()
        coEvery { localDataSource.getAllCountries() } returns listOf(localBrazilEntity)
        coEvery { localDataSource.getAllCountriesLastUpdatedAtMillis() } returns now

        val result = repository.getCountries()
        assertTrue(result is AppResult.Success)

        val countries = (result as AppResult.Success).data
        assertEquals(1, countries.size)
        assertEquals(listOf("Brasil"), countries.map{it.name})

        coVerify(exactly = 0) { remoteDataSource.getAllCountries() }
        coVerify(exactly = 0) { localDataSource.replaceAllCountries(any(), any()) }
    }

    @Test
    fun `should fetch remote and replace local when all countries cache is expired`() = runTest {
        val expiredUpdatedAt = System.currentTimeMillis() - (cacheTtlMillis + 1_000L)
        coEvery { localDataSource.getAllCountries() } returns listOf(localBrazilEntity)
        coEvery { localDataSource.getAllCountriesLastUpdatedAtMillis() } returns expiredUpdatedAt
        coEvery { remoteDataSource.getAllCountries() } returns listOf(remoteBrazilDto)
        coEvery { localDataSource.replaceAllCountries(any(), any()) } returns Unit

        val result = repository.getCountries()
        assertTrue(result is AppResult.Success)

        val countries = (result as AppResult.Success).data
        assertEquals(listOf("Brasil"), countries.map { it.name })

        coVerify(exactly = 1) { remoteDataSource.getAllCountries() }
        coVerify(exactly = 1) { localDataSource.replaceAllCountries(any(), any()) }
    }

    @Test
    fun `should return local fallback when all countries remote fails and local exists`() = runTest {
        val expiredUpdatedAt = System.currentTimeMillis() - (cacheTtlMillis + 1_000L)

        coEvery { localDataSource.getAllCountries() } returns listOf(localBrazilEntity)
        coEvery { localDataSource.getAllCountriesLastUpdatedAtMillis() } returns expiredUpdatedAt
        coEvery { remoteDataSource.getAllCountries() } throws IOException("no internet")

        val result = repository.getCountries()
        assertTrue(result is AppResult.Success)

        val countries = (result as AppResult.Success).data
        assertEquals(listOf("Brasil"), countries.map { it.name })

        coVerify(exactly = 1) { remoteDataSource.getAllCountries() }
        coVerify(exactly = 0) { localDataSource.replaceAllCountries(any(), any()) }
    }

    @Test
    fun `should return no internet error when local is empty and remote fails with IOException`() = runTest {
        coEvery { localDataSource.getAllCountries() } returns emptyList()
        coEvery { localDataSource.getAllCountriesLastUpdatedAtMillis() } returns null
        coEvery { remoteDataSource.getAllCountries() } throws IOException("no internet")

        val result = repository.getCountries()

        assertTrue(result is AppResult.Error)
        assertEquals(AppError.NoInternet, (result as AppResult.Error).error)
    }

    @Test
    fun `should return NotFound when local is empty and remote fails with http 404`() = runTest {
        val httpException = HttpException(Response.error<Any>(404, "".toResponseBody()))
        coEvery { localDataSource.getAllCountries() } returns emptyList()
        coEvery { localDataSource.getAllCountriesLastUpdatedAtMillis() } returns null
        coEvery { remoteDataSource.getAllCountries() } throws httpException

        val result = repository.getCountries()

        assertTrue(result is AppResult.Error)
        assertEquals(AppError.NotFound, (result as AppResult.Error).error)
    }

    @Test
    fun `should filter countries with null name when fetching all countries from remote`() = runTest {
        val expiredUpdatedAt = System.currentTimeMillis() - (cacheTtlMillis + 1_000L)
        coEvery { remoteDataSource.getAllCountries() } returns remoteListWithNull
        coEvery { localDataSource.getAllCountriesLastUpdatedAtMillis() } returns expiredUpdatedAt
        coEvery { localDataSource.getAllCountries() } returns emptyList()
        coEvery { localDataSource.replaceAllCountries(any(), any()) } returns Unit

        val result = repository.getCountries()
        assertTrue(result is AppResult.Success)

        val countries = (result as AppResult.Success).data
        assertEquals(1, countries.size)
        assertEquals("Brasil", countries.first().name)
    }

    @Test
    fun `should return local country when country cache is valid`() = runTest {
        val now = System.currentTimeMillis()
        coEvery { localDataSource.getCountryByCode("BRA") } returns localBrazilEntity
        coEvery { localDataSource.getCountryLastUpdatedAtMillis("BRA") } returns now

        val result = repository.getCountry("BRA")
        assertTrue(result is AppResult.Success)

        assertEquals("Brasil", (result as AppResult.Success).data.name)
        coVerify(exactly = 0) { remoteDataSource.getCountry(any()) }
    }

    @Test
    fun `should fetch remote country and upsert local when country cache is expired`() = runTest {
        val expiredUpdatedAt = System.currentTimeMillis() - (cacheTtlMillis + 1_000L)
        coEvery { localDataSource.getCountryByCode("BRA") } returns localBrazilEntity
        coEvery { localDataSource.getCountryLastUpdatedAtMillis("BRA") } returns expiredUpdatedAt
        coEvery { remoteDataSource.getCountry("BRA") } returns remoteBrazilDto
        coEvery { localDataSource.upsertCountry(any(), any()) } returns Unit

        val result = repository.getCountry("BRA")
        assertTrue(result is AppResult.Success)

        assertEquals("Brasil", (result as AppResult.Success).data.name)
        coVerify(exactly = 1) { localDataSource.upsertCountry(any(), any()) }
    }

    @Test
    fun `should return local country fallback when country remote fails and local exists`() = runTest {
        val expiredUpdatedAt = System.currentTimeMillis() - (cacheTtlMillis + 1_000L)
        coEvery { localDataSource.getCountryByCode("BRA") } returns localBrazilEntity
        coEvery { localDataSource.getCountryLastUpdatedAtMillis("BRA") } returns expiredUpdatedAt
        coEvery { remoteDataSource.getCountry("BRA") } throws IOException("no internet")

        val result = repository.getCountry("BRA")
        assertTrue(result is AppResult.Success)
        assertEquals("Brasil", (result as AppResult.Success).data.name)
    }

    @Test
    fun `should return no internet when country local is missing and remote fails`() = runTest {
        coEvery { localDataSource.getCountryByCode("BRA") } returns null
        coEvery { remoteDataSource.getCountry("BRA") } throws IOException("no internet")

        val result = repository.getCountry("BRA")
        assertTrue(result is AppResult.Error)
        assertEquals(AppError.NoInternet, (result as AppResult.Error).error)
    }

    @Test
    fun `should return invalid data when remote country cannot be converted to entity`() = runTest {
        coEvery { localDataSource.getCountryByCode("BRA") } returns null
        coEvery { remoteDataSource.getCountry("BRA") } returns remoteNullDto

        val result = repository.getCountry("BRA")
        assertTrue(result is AppResult.Error)
        assertEquals(AppError.InvalidData, (result as AppResult.Error).error)
    }

    @Test
    fun `should return local details with resolved border countries when details cache is valid`() = runTest {
        val now = System.currentTimeMillis()
        val localDetails = brazilCountryDetails
        val borderCodes = listOf("ARG", "URY")

        coEvery { localDataSource.getCountryDetails("BRA") } returns (localDetails to borderCodes)
        coEvery { localDataSource.getCountryDetailsLastUpdatedAtMillis("BRA") } returns now
        coEvery { localDataSource.getCountriesByCodes(borderCodes) } returns fakeBordersCountries

        val result = repository.getCountryDetails("BRA")
        assertTrue(result is AppResult.Success)

        val details = (result as AppResult.Success).data
        assertEquals("Brasil", details.name)
        assertEquals(listOf("ARG", "URY"), details.borders?.codes)
        assertEquals(listOf("Argentina", "Uruguai"), details.borders?.countries?.map { it.name })

        coVerify(exactly = 0) { remoteDataSource.getCountryDetails(any()) }
    }

    @Test
    fun `should fetch remote details and upsert local when details cache is expired`() = runTest {
        val expired = System.currentTimeMillis() - (cacheTtlMillis + 1_000L)
        val remoteDetails = remoteBrazilDetailsDto

        coEvery { localDataSource.getCountryDetails("BRA") } returns (brazilCountryDetails to listOf("ARG", "URY"))
        coEvery { localDataSource.getCountryDetailsLastUpdatedAtMillis("BRA") } returns expired
        coEvery { remoteDataSource.getCountryDetails("BRA") } returns remoteDetails
        coEvery { localDataSource.upsertCountryDetails(any(), any(), any(), any()) } returns Unit

        val result = repository.getCountryDetails("BRA")
        assertTrue(result is AppResult.Success)

        val details = (result as AppResult.Success).data
        assertEquals("Brasil", details.name)
        assertEquals(listOf("ARG", "URY"), details.borders?.codes)
        assertEquals(null, details.borders?.countries)

        coVerify(exactly = 1) {
            localDataSource.upsertCountryDetails(
                code = "BRA",
                details = any(),
                borderCodes = listOf("ARG", "URY"),
                updatedAtMillis = any()
            )
        }
    }
}