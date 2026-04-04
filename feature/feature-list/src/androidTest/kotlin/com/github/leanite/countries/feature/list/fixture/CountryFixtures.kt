package com.github.leanite.countries.feature.list.fixture

import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryCode
import com.github.leanite.countries.core.domain.model.Location

val brazilCountryForTest = Country(
    code = CountryCode("BRA"),
    name = "Brasil",
    flagUrl = null,
    location = Location(-14.235, -51.9253),
    region = "Americas",
    area = 8515767.0
)

val argentinaCountryForTest = Country(
    code = CountryCode("ARG"),
    name = "Argentina",
    flagUrl = null,
    location = Location(-38.4161, -63.6167),
    region = "Americas",
    area = 2780400.0
)

val germanyCountryForTest = Country(
    code = CountryCode("DEU"),
    name = "Alemanha",
    flagUrl = null,
    location = Location(51.1657, 10.4515),
    region = "Europe",
    area = 357114.0
)

val allCountriesForTest = listOf(argentinaCountryForTest, brazilCountryForTest, germanyCountryForTest)
