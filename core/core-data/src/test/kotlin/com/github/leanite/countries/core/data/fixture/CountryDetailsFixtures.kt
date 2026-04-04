package com.github.leanite.countries.core.data.fixture

import com.github.leanite.countries.core.data.model.local.CountryDetailsEntity
import com.github.leanite.countries.core.data.model.network.CountryDetailsDTO
import com.github.leanite.countries.core.data.model.network.PortugueseLanguageDTO
import com.github.leanite.countries.core.data.model.network.TranslationsDTO
import com.github.leanite.countries.core.domain.model.Border
import com.github.leanite.countries.core.domain.model.CountryDetails
import com.github.leanite.countries.core.domain.model.Currency
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

val brazilCountryDetails = CountryDetails(
    name = "Brasil",
    officialName = "República Federativa do Brasil",
    flagUrl = "https://flagcdn.com/br.svg",
    capitals = listOf("Brasília"),
    region = "South America",
    timezones = listOf("UTC-03:00"),
    area = 8_515_767.0,
    population = 203_080_756L,
    currencies = listOf(Currency(id = "BRL", name = "Brazilian real")),
    borders = Border(codes = listOf("ARG", "URY"), countries = null)
)

internal val remoteBrazilDetailsDto = CountryDetailsDTO(
    cca3 = "BRA",
    translations = TranslationsDTO(
        PortugueseLanguageDTO(
            common = "Brasil",
            official = "República Federativa do Brasil"
        )
    ),
    capitals = listOf("Brasília"),
    region = "South America",
    timezones = listOf("UTC-03:00"),
    area = 8_515_767.0,
    population = 203_080_756L,
    currencies = buildJsonObject {
        put(
            "BRL",
            buildJsonObject {
                put("name", JsonPrimitive("Brazilian real"))
            }
        )
    },
    borders = listOf("ARG", "URY")
)

internal val brazilCountryDetailsEntity = CountryDetailsEntity(
    code = "BRA",
    name = "Brasil",
    officialName = "República Federativa do Brasil",
    capitalsJson = "[\"Brasília\"]",
    region = "South America",
    timezonesJson = "[\"UTC-03:00\"]",
    area = 8515767.0,
    population = 203080756L,
    currenciesJson = "[{\"id\":\"BRL\",\"name\":\"Brazilian real\"}]",
    bordersJson = "[\"ARG\",\"URY\"]",
    flagUrl = "https://flagcdn.com/br.svg"
)

internal val emptyCountryDetailsEntity = CountryDetailsEntity(
    code = "BRA",
    name = "Brasil",
    officialName = null,
    capitalsJson = null,
    region = null,
    timezonesJson = null,
    area = null,
    population = null,
    currenciesJson = null,
    bordersJson = null,
    flagUrl = null
)

internal val countryDetailsEntityWithBorders = CountryDetailsEntity(
    code = "BRA",
    name = "Brasil",
    officialName = null,
    capitalsJson = null,
    region = null,
    timezonesJson = null,
    area = null,
    population = null,
    currenciesJson = null,
    bordersJson = "[\"ARG\",\"URY\"]",
    flagUrl = null
)

internal val emptyCountryDetailsDto = CountryDetailsDTO(
    translations = null,
    capitals = null,
    region = null,
    timezones = null,
    area = null,
    population = null,
    currencies = null,
    borders = null
)

internal val countryDetailsDtoWithNullCurrencyName = CountryDetailsDTO(
    translations = TranslationsDTO(PortugueseLanguageDTO(common = "X")),
    currencies = buildJsonObject {
        put("XXX", buildJsonObject { })
    }
)
