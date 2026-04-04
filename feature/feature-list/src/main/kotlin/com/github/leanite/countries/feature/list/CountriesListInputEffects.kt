package com.github.leanite.countries.feature.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun CountriesListInputEffects(
    onAction: (CountriesListAction) -> Unit,
) {
    LaunchedEffect(Unit) { onAction(CountriesListAction.Load) }
}
