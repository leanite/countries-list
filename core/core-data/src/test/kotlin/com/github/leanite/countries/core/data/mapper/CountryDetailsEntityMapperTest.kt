package com.github.leanite.countries.core.data.mapper

import com.github.leanite.countries.core.data.fixture.brazilCountryDetails
import com.github.leanite.countries.core.data.fixture.brazilCountryDetailsEntity
import com.github.leanite.countries.core.data.fixture.countryDetailsEntityWithBorders
import com.github.leanite.countries.core.data.fixture.emptyCountryDetailsEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CountryDetailsEntityMapperTest {

    @Test
    fun `should map details to entity and serialize json fields`() {
        val entity = brazilCountryDetails.toEntity(code = "BRA", borderCodes = listOf("ARG", "URY"))

        assertEquals("BRA", entity.code)
        assertEquals("Brasil", entity.name)
        assertEquals("República Federativa do Brasil", entity.officialName)
        assertEquals("South America", entity.region)
        assertEquals(8_515_767.0, entity.area ?: 0.0, 0.0)
        assertEquals(203_080_756L, entity.population)
        assertEquals("[\"ARG\",\"URY\"]", entity.bordersJson)
    }

    @Test
    fun `should map entity to domain without borders`() {
        val result = brazilCountryDetailsEntity.toDomainWithoutBorders()

        assertEquals("Brasil", result.name)
        assertEquals("República Federativa do Brasil", result.officialName)
        assertEquals(listOf("Brasília"), result.capitals)
        assertEquals("South America", result.region)
        assertEquals(listOf("UTC-03:00"), result.timezones)
        assertEquals(8_515_767.0, result.area ?: 0.0, 0.0)
        assertEquals(203_080_756L, result.population)
        assertEquals("BRL", result.currencies?.firstOrNull()?.id)
        assertNull(result.borders)
    }

    @Test
    fun `should return empty border codes when borders json is null`() {
        val result = emptyCountryDetailsEntity.getBorderCodes()

        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun `should decode border codes from borders json`() {
        val result = countryDetailsEntityWithBorders.getBorderCodes()

        assertEquals(listOf("ARG", "URY"), result)
    }
}
