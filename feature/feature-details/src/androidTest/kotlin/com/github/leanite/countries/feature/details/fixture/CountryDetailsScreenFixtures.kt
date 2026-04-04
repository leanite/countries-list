package com.github.leanite.countries.feature.details.fixture

import com.github.leanite.countries.core.domain.model.Border
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryCode
import com.github.leanite.countries.core.domain.model.CountryDetails
import com.github.leanite.countries.core.domain.model.Currency
import com.github.leanite.countries.feature.details.CountryDetailsContentState
import com.github.leanite.countries.feature.details.CountryDetailsUiState

val brazilDetailsForTest = CountryDetails(
    name = "Brasil",
    officialName = "República Federativa do Brasil",
    capitals = listOf("Brasília"),
    region = "South America",
    timezones = listOf("UTC-03:00"),
    area = 8_515_767.0,
    population = 203_080_756L,
    currencies = listOf(Currency(id = "BRL", name = "Real brasileiro")),
    borders = Border(
        codes = listOf("ARG"),
        countries = listOf(
            Country(code = CountryCode("ARG"), name = "Argentina")
        )
    )
)

val loadingUiState = CountryDetailsUiState(
    contentState = CountryDetailsContentState.Loading,
    countryCode = "BRA"
)

val errorUiState = CountryDetailsUiState(
    contentState = CountryDetailsContentState.Error,
    countryCode = "BRA"
)

val successUiState = CountryDetailsUiState(
    contentState = CountryDetailsContentState.Success,
    countryCode = "BRA",
    countryDetails = brazilDetailsForTest
)

val successWithoutBordersUiState = CountryDetailsUiState(
    contentState = CountryDetailsContentState.Success,
    countryCode = "BRA",
    countryDetails = brazilDetailsForTest.copy(borders = null)
)

val argentinaCountryForTest = Country(
    code = CountryCode("ARG"),
    name = "Argentina",
    flagUrl = null
)
