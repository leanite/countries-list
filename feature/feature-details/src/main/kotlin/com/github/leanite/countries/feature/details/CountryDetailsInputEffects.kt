package com.github.leanite.countries.feature.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun CountryDetailsInputEffects(
    countryCode: String,
    onAction: (CountryDetailsAction) -> Unit,
) {
    LaunchedEffect(countryCode) { onAction(CountryDetailsAction.Load(countryCode)) }
}
