package com.github.leanite.countries.core.data.mapper

import com.github.leanite.countries.core.data.fixture.CountryDTOFixtures.emptyLatLngDto
import com.github.leanite.countries.core.data.fixture.CountryDTOFixtures.faultyLatLngDto
import com.github.leanite.countries.core.data.fixture.CountryDTOFixtures.remoteBrazilDto
import com.github.leanite.countries.core.data.fixture.CountryDTOFixtures.remoteNullDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CountryDTOMapperTest {
    @Test
    fun `should map full dto to domain`() {
        val result = remoteBrazilDto.toDomain()

        assertEquals("BRA", result.code?.value)
        assertEquals("Brasil", result.name)
        assertEquals("https://flagcdn.com/br.svg", result.flagUrl)
        assertNotNull(result.location)
        assertEquals(-10.0, result.location?.latitude ?: 0.0, 0.0)
        assertEquals(-55.0, result.location?.longitude ?: 0.0, 0.0)
        assertEquals("South America", result.region)
    }

    @Test
    fun `should map null fields without crashing`() {
        val result = remoteNullDto.toDomain()

        assertNull(result.code)
        assertNull(result.name)
        assertNull(result.flagUrl)
        assertNull(result.location)
        assertNull(result.region)
    }

    @Test
    fun `should keep location null when latlng is empty`() {
        val result = emptyLatLngDto.toDomain()

        assertEquals("NOC", result.code?.value)
        assertEquals("No Coordinates", result.name)
        assertNull(result.location)
    }

    @Test
    fun `should keep location null when latlng has only one value`() {
        val result = faultyLatLngDto.toDomain()

        assertEquals("PRT", result.code?.value)
        assertEquals("Partial Coordinates", result.name)
        assertNull(result.location)
    }
}
