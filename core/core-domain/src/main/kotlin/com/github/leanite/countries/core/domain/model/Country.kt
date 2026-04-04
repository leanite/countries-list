package com.github.leanite.countries.core.domain.model

data class Country(
    val code: CountryCode? = null,
    val name: String? = null,
    val flagUrl: String? = null,
    val location: Location? = null,
    val region: String? = null,
    val area: Double? = null,
)

@JvmInline
value class CountryCode(val value: String)
