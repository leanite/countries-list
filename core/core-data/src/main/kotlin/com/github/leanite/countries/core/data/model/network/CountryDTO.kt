package com.github.leanite.countries.core.data.model.network

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SuppressLint("UnsafeOptInUsageError")
internal data class CountryDTO(
    val cca3: String? = null,
    val translations: TranslationsDTO? = null,
    val flags: FlagsDTO? = null,
    val latlng: List<Double>? = null,
    val region: String? = null,
    val area: Double? = null,
)

@Serializable
@SuppressLint("UnsafeOptInUsageError")
internal data class FlagsDTO(
    @SerialName("png")
    val png: String? = null,
    @SerialName("svg")
    val svg: String? = null,
    @SerialName("alt")
    val alt: String? = null
)
