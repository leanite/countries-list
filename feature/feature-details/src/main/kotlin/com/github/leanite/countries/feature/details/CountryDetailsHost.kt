package com.github.leanite.countries.feature.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CountryDetailsHost(
    countryCode: String,
    onBackClick: () -> Unit,
    onNavigateToCountryDetails: (String) -> Unit,
    onOpenOnMap: (String) -> Unit,
    viewModel: CountryDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    CountryDetailsInputEffects(
        countryCode = countryCode,
        onAction = viewModel::onAction
    )

    CountryDetailsEventEffects(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
        onNavigateToCountryDetails = onNavigateToCountryDetails,
        onOpenOnMap = onOpenOnMap
    )

    Box(modifier = Modifier.fillMaxSize()) {
        CountryDetailsScreen(
            uiState = uiState,
            onAction = viewModel::onAction,
            onBackClick = onBackClick,
        )
        SnackbarHost(hostState = snackbarHostState)
    }
}