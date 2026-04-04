package com.github.leanite.countries.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Immutable
@Serializable
sealed interface CountriesNavKey : NavKey

@Immutable
@Serializable
data object CountryList : CountriesNavKey

@Immutable
@Serializable
data class CountryDetails(val code: String) : CountriesNavKey
