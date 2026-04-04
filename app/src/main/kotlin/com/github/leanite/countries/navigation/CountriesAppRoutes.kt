package com.github.leanite.countries.navigation

import androidx.compose.runtime.Immutable


@Immutable
object CountriesAppRoute {
    const val COUNTRY_LIST = "country_list"
    const val COUNTRY_CODE_ARG = "countryCode"
    const val COUNTRY_DETAILS = "country_details/{$COUNTRY_CODE_ARG}"

    fun countryDetails(countryCode: String): String {
        return "country_details/$countryCode"
    }
}

@Immutable
object CountriesAppKey {
    const val FOCUS_COUNTRY_RESULT_KEY = "focusCountry"
}