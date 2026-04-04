package com.github.leanite.countries.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.leanite.countries.feature.details.CountryDetailsHost
import com.github.leanite.countries.feature.list.CountriesListHost

@Composable
fun CountriesAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CountriesAppRoute.COUNTRY_LIST,
    ) {
        countryListDestination(
            onNavigateToCountryDetails = { code ->
                navController.navigate(CountriesAppRoute.countryDetails(code))
            }
        )

        countryDetailsDestination(
            onBackClick = { navController.popBackStack() },
            onNavigateToCountryDetails = { code ->
                navController.navigate(CountriesAppRoute.countryDetails(code))
            },
            onOpenOnMap = { code ->
                val listEntry = navController.getBackStackEntry(CountriesAppRoute.COUNTRY_LIST)
                listEntry.savedStateHandle[CountriesAppKey.FOCUS_COUNTRY_RESULT_KEY] = code
                navController.popBackStack(
                    route = CountriesAppRoute.COUNTRY_LIST,
                    inclusive = false
                )
            }
        )
    }
}
