package com.github.leanite.countries.core.data.datasource

import androidx.room.withTransaction
import com.github.leanite.countries.core.data.fixture.CountryEntityFixtures.localBrazilEntity
import com.github.leanite.countries.core.data.fixture.CountryEntityFixtures.entityList
import com.github.leanite.countries.core.data.fixture.allCountriesCacheMetadata
import com.github.leanite.countries.core.data.fixture.brazilCountryDetails
import com.github.leanite.countries.core.data.fixture.brazilCountryDetailsEntity
import com.github.leanite.countries.core.data.fixture.countryCacheBraMetadata
import com.github.leanite.countries.core.data.fixture.countryDetailsCacheBraMetadata
import com.github.leanite.countries.core.data.local.CacheMetadataDAO
import com.github.leanite.countries.core.data.local.CountriesDatabase
import com.github.leanite.countries.core.data.local.CountryDAO
import com.github.leanite.countries.core.data.local.CountryDetailsDAO
import com.github.leanite.countries.core.data.model.local.CountryEntity
import com.github.leanite.countries.core.domain.model.CountryDetails
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class CountryLocalDataSourceImplTest {
    private val db: CountriesDatabase = mockk(relaxed = true)
    private val countryDao: CountryDAO = mockk(relaxed = true)
    private val detailsDao: CountryDetailsDAO = mockk(relaxed = true)
    private val metadataDao: CacheMetadataDAO = mockk(relaxed = true)

    private lateinit var dataSource: CountryLocalDataSourceImpl

    @Before
    fun setup() {
        mockkStatic("androidx.room.RoomDatabaseKt")

        /*
            mockar "db.withTransaction{ block }"
            args[0] = receiver (db)
            args[1] = block da transação (lambda suspend)
         */
        coEvery { db.withTransaction(any<suspend () -> Unit>()) } coAnswers {
            val block = secondArg<suspend () -> Any?>()
            block.invoke()
        }
        every { db.countryDao() } returns countryDao
        every { db.countryDetailsDao() } returns detailsDao
        every { db.cacheMetadataDao() } returns metadataDao
        dataSource = CountryLocalDataSourceImpl(db)
    }


    @After
    fun tearDown() {
        unmockkStatic("androidx.room.RoomDatabaseKt")
    }

    @Test
    fun `should return all countries from dao`() = runTest {
        coEvery { countryDao.getAll() } returns entityList

        val result = dataSource.getAllCountries()

        assertEquals(entityList, result)
        coVerify(exactly = 1) { countryDao.getAll() }
    }

    @Test
    fun `should return country by code`() = runTest {
        val entity = localBrazilEntity
        coEvery { countryDao.getByCode("BRA") } returns entity

        val result = dataSource.getCountryByCode("BRA")

        assertEquals(entity, result)
    }

    @Test
    fun `should return empty list and skip dao when codes list is empty`() = runTest {
        val result = dataSource.getCountriesByCodes(emptyList())

        assertEquals(emptyList<CountryEntity>(), result)
        coVerify(exactly = 0) { countryDao.getByCodes(any()) }
    }

    @Test
    fun `should replace countries and update all countries metadata`() = runTest {
        val items = listOf(localBrazilEntity)
        val updatedAt = 123_456L

        dataSource.replaceAllCountries(items, updatedAt)

        coVerify(exactly = 1) { countryDao.clearAll() }
        coVerify(exactly = 1) { countryDao.upsertAll(items) }
        coVerify(exactly = 1) {
            metadataDao.upsert(
                match {
                    it.key == "countries_all" && it.updatedAtMillis == updatedAt
                }
            )
        }
    }

    @Test
    fun `should upsert one country and update country metadata`() = runTest {
        val item = localBrazilEntity
        val updatedAt = 555L

        dataSource.upsertCountry(item, updatedAt)

        coVerify(exactly = 1) { countryDao.upsert(item) }
        coVerify(exactly = 1) {
            metadataDao.upsert(
                match { it.key == "country_BRA" && it.updatedAtMillis == updatedAt }
            )
        }
    }

    @Test
    fun `should return null when all countries cache metadata is missing`() = runTest {
        coEvery { metadataDao.get("countries_all") } returns null

        val result = dataSource.getAllCountriesLastUpdatedAtMillis()

        assertNull(result)
    }

    @Test
    fun `should return timestamp when cache metadata exists`() = runTest {
        coEvery { metadataDao.get("countries_all") } returns allCountriesCacheMetadata

        val result = dataSource.getAllCountriesLastUpdatedAtMillis()

        assertEquals(allCountriesCacheMetadata.updatedAtMillis, result)
    }

    @Test
    fun `should return timestamp when country metadata exists`() = runTest {
        coEvery { metadataDao.get("country_BRA") } returns countryCacheBraMetadata

        val result = dataSource.getCountryLastUpdatedAtMillis("BRA")

        assertEquals(countryCacheBraMetadata.updatedAtMillis, result)
    }

    @Test
    fun `should return details and border codes when details exists`() = runTest {
        val entity = brazilCountryDetailsEntity
        coEvery { detailsDao.getByCode("BRA") } returns entity

        val result = dataSource.getCountryDetails("BRA")

        assertEquals("Brasil", result?.first?.name)
        assertEquals(listOf("ARG", "URY"), result?.second)
    }

    @Test
    fun `should upsert details and update details metadata`() = runTest {
        val details = brazilCountryDetails
        val updatedAt = 999L

        dataSource.upsertCountryDetails(
            code = "BRA",
            details = details,
            borderCodes = listOf("ARG"),
            updatedAtMillis = updatedAt
        )

        coVerify(exactly = 1) {
            detailsDao.upsert(
                match { it.code == "BRA" && it.bordersJson?.contains("ARG") == true }
            )
        }
        coVerify(exactly = 1) {
            metadataDao.upsert(
                match { it.key == "country_details_BRA" && it.updatedAtMillis == updatedAt }
            )
        }
    }

    @Test
    fun `should return details timestamp when details metadata exists`() = runTest {
        coEvery { metadataDao.get("country_details_BRA") } returns countryDetailsCacheBraMetadata

        val result = dataSource.getCountryDetailsLastUpdatedAtMillis("BRA")

        assertEquals(countryDetailsCacheBraMetadata.updatedAtMillis, result)
    }
}