package com.github.leanite.countries.core.data.model.network

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SuppressLint("UnsafeOptInUsageError")
internal data class TranslationsDTO(
    @SerialName("por")
    val portuguese: PortugueseLanguageDTO? = null,
)

@Serializable
@SuppressLint("UnsafeOptInUsageError")
internal data class PortugueseLanguageDTO(
    @SerialName("common")
    val common: String? = null,
    @SerialName("official")
    val official: String? = null,
)
