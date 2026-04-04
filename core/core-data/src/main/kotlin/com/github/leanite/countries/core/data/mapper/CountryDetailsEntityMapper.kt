package com.github.leanite.countries.core.data.mapper

import com.github.leanite.countries.core.data.model.local.CountryDetailsEntity
import com.github.leanite.countries.core.data.model.network.CurrencyDTO
import com.github.leanite.countries.core.domain.model.CountryDetails
import com.github.leanite.countries.core.domain.model.Currency
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

internal fun CountryDetails.toEntity(code: String, borderCodes: List<String>): CountryDetailsEntity {
    return CountryDetailsEntity(
        code = code,
        name = name.orEmpty(),
        officialName = officialName,
        capitalsJson = capitals?.let { json.encodeToString(it) },
        region = region,
        timezonesJson = timezones?.let { json.encodeToString(it) },
        area = area,
        population = population,
        currenciesJson = currencies
            ?.map { CurrencyDTO(id = it.id, name = it.name) }
            ?.let { json.encodeToString(it) },
        bordersJson = json.encodeToString(borderCodes),
        flagUrl = flagUrl
    )
}

internal fun CountryDetailsEntity.toDomainWithoutBorders(): CountryDetails {
    val capitals = capitalsJson?.let { json.decodeFromString<List<String>>(it) }
    val timezones = timezonesJson?.let { json.decodeFromString<List<String>>(it) }
    val currencies = currenciesJson
        ?.let { json.decodeFromString<List<CurrencyDTO>>(it) }
        ?.map { Currency(id = it.id, name = it.name) }

    return CountryDetails(
        name = name,
        officialName = officialName,
        flagUrl = flagUrl,
        capitals = capitals,
        region = region,
        timezones = timezones,
        area = area,
        population = population,
        currencies = currencies,
        borders = null
    )
}

internal fun CountryDetailsEntity.getBorderCodes(): List<String> {
    return bordersJson?.let { json.decodeFromString<List<String>>(it) }.orEmpty()
}
