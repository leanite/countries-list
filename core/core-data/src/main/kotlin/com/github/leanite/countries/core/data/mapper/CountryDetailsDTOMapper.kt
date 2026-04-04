package com.github.leanite.countries.core.data.mapper

import com.github.leanite.countries.core.data.model.network.CountryDetailsDTO
import com.github.leanite.countries.core.domain.model.Border
import com.github.leanite.countries.core.domain.model.CountryDetails
import com.github.leanite.countries.core.domain.model.Currency
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun CountryDetailsDTO.toDomain(): CountryDetails {
    val translations = this.translations?.portuguese

    return CountryDetails(
        name = translations?.common,
        officialName = translations?.official,
        flagUrl = flags?.svg,
        capitals = capitals,
        region = region,
        timezones = timezones.orEmpty(),
        area = area,
        population = population,
        currencies = mapCurrencies(currencies),
        borders = mapBorders(borders)
    )
}

internal fun mapCurrencies(jsonCurrencies: JsonObject?): List<Currency>? {
    if (jsonCurrencies == null) return null

    val currencies = mutableListOf<Currency>()

    jsonCurrencies.forEach { key, jsonValue ->
        val name = jsonValue.jsonObject["name"]?.jsonPrimitive?.contentOrNull
        currencies.add(Currency(key, name))
    }
    return currencies.takeIf { it.isNotEmpty() }
}

private fun mapBorders(borderCodes: List<String>?): Border? {
    return if (borderCodes != null) {
        Border(codes = borderCodes, countries = null)
    } else {
        null
    }
}
