package com.github.leanite.countries.feature.list.fixture

import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryCode
import com.github.leanite.countries.core.domain.model.Location
import com.github.leanite.countries.feature.list.ui.model.CountrySectionList

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

    val countriesMixed = listOf(
        countryWithName("Argentina"),
        countryWithName("Áustria"),
        countryWithName("Brasil"),
        countryWithName("Chile"),
        countryWithName("Éire"),
        countryWithName("123 Land")
    )

    val brazil = Country(
        code = CountryCode("BRA"),
        name = "Brasil",
        location = Location(-14.235, -51.9253),
        region = "Americas",
        area = 8515767.0
    )

    val argentina = Country(
        code = CountryCode("ARG"),
        name = "Argentina",
        location = Location(-38.4161, -63.6167),
        region = "Americas",
        area = 2780400.0
    )

    val germany = Country(
        code = CountryCode("DEU"),
        name = "Alemanha",
        location = Location(51.1657, 10.4515),
        region = "Europe",
        area = 357114.0
    )

    val allCountries = listOf(argentina, brazil, germany)

    val emptySections = CountrySectionList(emptyList(), emptyList())

    val brazilWithNoLocation = brazil.copy(location = null)

    val brazilWithNoCode = brazil.copy(code = null)
}
