package com.github.leanite.countries.core.data.mapper

import com.github.leanite.countries.core.data.fixture.CountryEntityFixtures.localBrazilEntity
import com.github.leanite.countries.core.data.fixture.argentinaCountry
import com.github.leanite.countries.core.data.fixture.countryWithWhitespace
import com.github.leanite.countries.core.data.fixture.faultyCountryWithNoCode
import com.github.leanite.countries.core.data.fixture.faultyCountryWithNoName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CountryEntityMapperTest {
    @Test
    fun `should map entity to domain`() {
        val result = localBrazilEntity.toDomain()

        assertEquals("BRA", result.code?.value)
        assertEquals("Brasil", result.name)
        assertEquals("https://flagcdn.com/br.svg", result.flagUrl)
        assertNotNull(result.location)
        assertEquals(-10.0, result.location?.latitude ?: 0.0, 0.0)
        assertEquals(-55.0, result.location?.longitude ?: 0.0, 0.0)
    }

    @Test
    fun `should map domain to entity`() {
        val result = argentinaCountry.toEntity()

        assertNotNull(result)
        assertEquals("ARG", result?.code)
        assertEquals("Argentina", result?.name)
        assertEquals("https://flagcdn.com/ar.svg", result?.flagUrl)
        assertEquals(-34.0, result?.latitude ?: 0.0, 0.0)
        assertEquals(-64.0, result?.longitude ?: 0.0, 0.0)
    }

    @Test
    fun `should return null when code is blank`() {
        val result = faultyCountryWithNoCode.toEntity()

        assertNull(result)
    }

    @Test
    fun `should return null when name is blank`() {
        val result = faultyCountryWithNoName.toEntity()

        assertNull(result)
    }

    @Test
    fun `should trim code and name when mapping to entity`() {
        val result = countryWithWhitespace.toEntity()

        assertEquals("BRA", result?.code)
        assertEquals("Brasil", result?.name)
    }
}
