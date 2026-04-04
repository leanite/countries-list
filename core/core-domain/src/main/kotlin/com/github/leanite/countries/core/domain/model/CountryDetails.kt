package com.github.leanite.countries.core.domain.model

data class CountryDetails(
    val name: String? = null,
    val officialName: String? = null,
    val flagUrl: String? = null,
    val capitals: List<String>? = null,
    val region: String? = null,
    val timezones: List<String>? = null,
    val area: Double? = null,
    val population: Long? = null,
    val currencies: List<Currency>? = null,
    val borders: Border? = null
)
