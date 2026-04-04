package com.github.leanite.countries.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.leanite.countries.feature.details.CountryDetailsHost
import com.github.leanite.countries.feature.list.CountriesListHost

fun NavGraphBuilder.countryListDestination(
    onNavigateToCountryDetails: (String) -> Unit
) {
    composable(route = CountriesAppRoute.COUNTRY_LIST) { backStackEntry ->
        val focusCountryCode by backStackEntry
            .savedStateHandle
            .getStateFlow<String?>(CountriesAppKey.FOCUS_COUNTRY_RESULT_KEY, null)
            .collectAsState()

        CountriesListHost(
            onNavigateToCountryDetails = { code -> onNavigateToCountryDetails(code) },
            focusCountryCode = focusCountryCode,
            onFocusConsumed = {
                backStackEntry.savedStateHandle[CountriesAppKey.FOCUS_COUNTRY_RESULT_KEY] = null
            }
        )
    }
}

fun NavGraphBuilder.countryDetailsDestination(
    onBackClick: () -> Unit,
    onOpenOnMap: (String) -> Unit,
    onNavigateToCountryDetails: (String) -> Unit
) {
    composable(
        route = CountriesAppRoute.COUNTRY_DETAILS,
        arguments = listOf(
            navArgument(CountriesAppRoute.COUNTRY_CODE_ARG) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val countryCode = backStackEntry.arguments
            ?.getString(CountriesAppRoute.COUNTRY_CODE_ARG)
            .orEmpty()

        CountryDetailsHost(
            countryCode = countryCode,
            onBackClick = onBackClick,
            onNavigateToCountryDetails = { code -> onNavigateToCountryDetails(code) },
            onOpenOnMap = { code -> onOpenOnMap(code) }
        )
    }
}