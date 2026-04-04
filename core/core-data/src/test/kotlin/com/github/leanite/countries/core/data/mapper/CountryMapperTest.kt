package com.github.leanite.countries.core.data.mapper

import com.github.leanite.countries.core.data.fixture.CountryDTOFixtures.remoteBrazilDto
import com.github.leanite.countries.core.data.fixture.CountryDTOFixtures.emptyLatLngDto
import com.github.leanite.countries.core.data.fixture.CountryDTOFixtures.faultyLatLngDto
import com.github.leanite.countries.core.data.fixture.CountryDTOFixtures.remoteNullDto
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Test

class CountryMapperTest {
    @Test
    fun `should map full dto to domain`() {
        val result = remoteBrazilDto.toDomain()

        assertEquals("Brasil", result.name)
        assertEquals("https://flagcdn.com/br.svg", result.flagUrl)
        assertNotNull(result.location)
        assertEquals(-10.0, result.location?.latitude ?: 0.0, 0.0)
        assertEquals(-55.0, result.location?.longitude ?: 0.0, 0.0)
    }

    @Test
    fun `should map null fields without crashing`() {
        val result = remoteNullDto.toDomain()

        assertNull(result.name)
        assertNull(result.flagUrl)
        assertNull(result.location)
    }

    @Test
    fun `should keep location null when latlng is empty`() {
        val result = emptyLatLngDto.toDomain()

        assertEquals("No Coordinates", result.name)
        assertEquals("https://flagcdn.com/test.svg", result.flagUrl)
        assertNull(result.location)
    }

    @Test
    fun `should keep location null when latlng has only one value`() {
        val result = faultyLatLngDto.toDomain()

        assertEquals("Partial Coordinates", result.name)
        assertEquals("https://flagcdn.com/partial.svg", result.flagUrl)
        assertNull(result.location)
    }
}