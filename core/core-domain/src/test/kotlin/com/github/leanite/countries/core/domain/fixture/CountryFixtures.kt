package com.github.leanite.countries.core.domain.fixture

import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryCode

internal object CountryFixtures {
    private fun countryWithName(name: String?): Country {
        return Country(
            code = null,
            name = name,
            flagUrl = null,
            location = null,
            area = null
        )
    }

    val countriesNotTrimmed = listOf(
        countryWithName("  Brasil  "),
        countryWithName("Áustria"),
        countryWithName("Argentina"),
        countryWithName(""),
        countryWithName("   "),
        countryWithName(null)
    )

    val brazilCountry = Country(code = CountryCode("BRA"), name = "Brasil", flagUrl = null, location = null)
    val argentinaCountry = Country(code = CountryCode("ARG"), name = "Argentina", flagUrl = null, location = null)
}
