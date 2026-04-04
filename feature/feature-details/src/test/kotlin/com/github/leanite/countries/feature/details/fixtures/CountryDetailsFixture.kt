package com.github.leanite.countries.feature.details.fixtures

import com.github.leanite.countries.core.domain.model.Border
import com.github.leanite.countries.core.domain.model.CountryDetails

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

val brazilCountryDetailsWithBorders = CountryDetails(
    name = "Brasil",
    officialName = "República Federativa do Brasil",
    capitals = listOf("Brasília"),
    region = "South America",
    timezones = listOf("UTC-03:00"),
    area = 8515767.0,
    population = 203080756L,
    currencies = null,
    borders = Border(codes = listOf("ARG", "URY"), countries = null)
)
