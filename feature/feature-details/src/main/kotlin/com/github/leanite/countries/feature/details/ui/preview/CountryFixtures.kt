package com.github.leanite.countries.feature.details.ui.preview

import com.github.leanite.countries.core.domain.model.Border
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryCode
import com.github.leanite.countries.core.domain.model.CountryDetails
import com.github.leanite.countries.core.domain.model.Currency
import com.github.leanite.countries.feature.details.CountryDetailsContentState
import com.github.leanite.countries.feature.details.CountryDetailsUiState

val countryFixture = Country(
    code = CountryCode("BRA"),
    name = "Brasil", flagUrl = "brasil.svg",
    location = null,
    area = 8515767.0
)

val detailsPreviewFixture = CountryDetails(
    name = "Brasil",
    officialName = "República Federativa do Brasil",
    capitals = listOf("Brasília"),
    region = "South America",
    timezones = listOf("UTC-03:00"),
    area = 8_515_767.0,
    population = 203_080_756L,
    currencies = listOf(Currency(id = "BRL", name = "Real brasileiro")),
    borders = Border(
        codes = listOf("ARG", "URY"),
        countries = listOf(
            Country(code = CountryCode("ARG"), name = "Argentina"),
            Country(code = CountryCode("URY"), name = "Uruguai")
        )
    )
)

val loadingPreviewUiState = CountryDetailsUiState(
    contentState = CountryDetailsContentState.Loading,
    countryCode = "BRA"
)

val errorPreviewUiState = CountryDetailsUiState(
    contentState = CountryDetailsContentState.Error,
    countryCode = "BRA"
)

val successPreviewUiState = CountryDetailsUiState(
    contentState = CountryDetailsContentState.Success,
    countryCode = "BRA",
    countryDetails = detailsPreviewFixture
)