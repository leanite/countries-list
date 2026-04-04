package com.github.leanite.countries.core.data.model.network

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
@SuppressLint("UnsafeOptInUsageError")
internal data class CountryDetailsDTO(
    val cca3: String? = null,
    val translations: TranslationsDTO? = null,
    @SerialName("capital")
    val capitals: List<String>? = null,
    val region: String? = null,
    val timezones: List<String>? = null,
    val area: Double? = null,
    val population: Long? = null,
    val currencies: JsonObject? = null,
    val borders: List<String>? = null,
    val flags: FlagsDTO? = null,
)

@Serializable
@SuppressLint("UnsafeOptInUsageError")
internal data class CurrencyDTO(
    val id: String?,
    val name: String?
)
