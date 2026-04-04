package com.github.leanite.countries.core.domain.fixture

import com.github.leanite.countries.core.domain.model.CountryDetails

internal object CountryDetailsFixtures {
    val brazilCountryDetails = CountryDetails(
        name = "Brasil",
        officialName = "República Federativa do Brasil",
        capitals = listOf("Brasília"),
        region = "South America",
        timezones = listOf("UTC-03:00"),
        area = 8515767.0,
        population = 203080756L,
        currencies = null,
        borders = null
    )
}
