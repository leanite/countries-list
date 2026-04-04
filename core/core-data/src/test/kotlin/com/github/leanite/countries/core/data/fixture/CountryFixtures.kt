package com.github.leanite.countries.core.data.fixture

import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryCode
import com.github.leanite.countries.core.domain.model.Location

val faultyCountryWithNoCode = Country(
    code = CountryCode(" "),
    name = "Brasil",
    flagUrl = null,
    location = null,
    area = 8515767.0,
)

val faultyCountryWithNoName = Country(
    code = CountryCode("BRA"),
    name = " ",
    flagUrl = null,
    location = null,
    area = 8515767.0,
)

val argentinaCountry = Country(
    code = CountryCode("ARG"),
    name = "Argentina",
    flagUrl = "https://flagcdn.com/ar.svg",
    location = Location(-34.0, -64.0),
    area = 2780400.0,
)

val countryWithWhitespace = Country(
    code = CountryCode(" BRA "),
    name = " Brasil ",
    flagUrl = null,
    location = null,
)
