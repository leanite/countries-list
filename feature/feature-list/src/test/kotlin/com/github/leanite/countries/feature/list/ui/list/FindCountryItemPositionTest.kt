package com.github.leanite.countries.feature.list.ui.list

import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryCode
import com.github.leanite.countries.feature.list.ui.model.CountrySection
import com.github.leanite.countries.feature.list.ui.model.CountrySectionList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FindCountryItemPositionTest {

    private val argentina = Country(code = CountryCode("ARG"), name = "Argentina")
    private val brazil = Country(code = CountryCode("BRA"), name = "Brasil")
    private val chile = Country(code = CountryCode("CHL"), name = "Chile")

    private val countries = listOf(argentina, brazil, chile)
    private val emptySections = CountrySectionList(emptyList(), emptyList())

    private val sections = CountrySectionList(
        sections = listOf(
            CountrySection('A', listOf(argentina)),
            CountrySection('B', listOf(brazil)),
            CountrySection('C', listOf(chile)),
        ),
        index = emptyList()
    )

    @Test
    fun `should find country by code without headers`() {
        val result = findCountryItemPosition(
            countryCode = "BRA",
            visibleCountries = countries,
            showSectionHeaders = false,
            sectionList = emptySections
        )

        assertEquals(1, result)
    }

    @Test
    fun `should find first country without headers`() {
        val result = findCountryItemPosition(
            countryCode = "ARG",
            visibleCountries = countries,
            showSectionHeaders = false,
            sectionList = emptySections
        )

        assertEquals(0, result)
    }

    @Test
    fun `should return null when code not found without headers`() {
        val result = findCountryItemPosition(
            countryCode = "XYZ",
            visibleCountries = countries,
            showSectionHeaders = false,
            sectionList = emptySections
        )

        assertNull(result)
    }

    @Test
    fun `should be case insensitive without headers`() {
        val result = findCountryItemPosition(
            countryCode = "bra",
            visibleCountries = countries,
            showSectionHeaders = false,
            sectionList = emptySections
        )

        assertEquals(1, result)
    }

    @Test
    fun `should account for section headers in position`() {
        // Layout: [header A=0] [ARG=1] [header B=2] [BRA=3] [header C=4] [CHL=5]
        val result = findCountryItemPosition(
            countryCode = "BRA",
            visibleCountries = countries,
            showSectionHeaders = true,
            sectionList = sections
        )

        assertEquals(3, result)
    }

    @Test
    fun `should find first country with headers`() {
        // Layout: [header A=0] [ARG=1]
        val result = findCountryItemPosition(
            countryCode = "ARG",
            visibleCountries = countries,
            showSectionHeaders = true,
            sectionList = sections
        )

        assertEquals(1, result)
    }

    @Test
    fun `should find last country with headers`() {
        // Layout: [header A=0] [ARG=1] [header B=2] [BRA=3] [header C=4] [CHL=5]
        val result = findCountryItemPosition(
            countryCode = "CHL",
            visibleCountries = countries,
            showSectionHeaders = true,
            sectionList = sections
        )

        assertEquals(5, result)
    }

    @Test
    fun `should return null when code not found with headers`() {
        val result = findCountryItemPosition(
            countryCode = "XYZ",
            visibleCountries = countries,
            showSectionHeaders = true,
            sectionList = sections
        )

        assertNull(result)
    }

}
