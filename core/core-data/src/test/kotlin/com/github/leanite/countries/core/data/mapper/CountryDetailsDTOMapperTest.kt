package com.github.leanite.countries.core.data.mapper

import com.github.leanite.countries.core.data.fixture.countryDetailsDtoWithNullCurrencyName
import com.github.leanite.countries.core.data.fixture.emptyCountryDetailsDto
import com.github.leanite.countries.core.data.fixture.remoteBrazilDetailsDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CountryDetailsDTOMapperTest {

    @Test
    fun `should map details dto with currencies and borders`() {
        val result = remoteBrazilDetailsDto.toDomain()

        assertEquals("Brasil", result.name)
        assertEquals("República Federativa do Brasil", result.officialName)
        assertEquals(listOf("Brasília"), result.capitals)
        assertEquals("South America", result.region)
        assertEquals(listOf("UTC-03:00"), result.timezones)
        assertEquals(8_515_767.0, result.area ?: 0.0, 0.0)
        assertEquals(203_080_756L, result.population)
        assertEquals("BRL", result.currencies?.firstOrNull()?.id)
        assertEquals("Brazilian real", result.currencies?.firstOrNull()?.name)
        assertEquals(listOf("ARG", "URY"), result.borders?.codes)
        assertNull(result.borders?.countries)
    }

    @Test
    fun `should map empty collections when dto fields are null`() {
        val result = emptyCountryDetailsDto.toDomain()

        assertNull(result.name)
        assertNull(result.officialName)
        assertNull(result.capitals)
        assertNull(result.region)
        assertEquals(emptyList<String>(), result.timezones)
        assertNull(result.currencies)
        assertNull(result.borders)
    }

    @Test
    fun `should map currency with null name`() {
        val result = countryDetailsDtoWithNullCurrencyName.toDomain()

        assertNotNull(result.currencies)
        assertEquals("XXX", result.currencies?.firstOrNull()?.id)
        assertNull(result.currencies?.firstOrNull()?.name)
    }
}
