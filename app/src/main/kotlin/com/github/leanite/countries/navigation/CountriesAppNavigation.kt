package com.github.leanite.countries.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.github.leanite.countries.feature.details.CountryDetailsHost
import com.github.leanite.countries.feature.list.CountriesListHost

@Composable
fun CountriesAppNavigation() {
    val backStack = rememberNavBackStack(CountryList)
    val navigator = remember(backStack) { CountriesNavigator(backStack) }
    var focusCountryCode by rememberSaveable { mutableStateOf<String?>(null) }

    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.goBack() },
        entryProvider = entryProvider {
            entry<CountryList> {
                CountriesListHost(
                    onNavigateToCountryDetails = { code -> navigator.navigate(CountryDetails(code)) },
                    focusCountryCode = focusCountryCode,
                    onFocusConsumed = { focusCountryCode = null }
                )
            }

            entry<CountryDetails> { key ->
                CountryDetailsHost(
                    countryCode = key.code,
                    onBackClick = { navigator.goBack() },
                    onNavigateToCountryDetails = { code -> navigator.navigate(CountryDetails(code)) },
                    onOpenOnMap = { code ->
                        focusCountryCode = code
                        navigator.popToRoot()
                    }
                )
            }
        }
    )
}
