package com.github.leanite.countries.feature.list

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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun CountriesListHost(
    onNavigateToCountryDetails: (String) -> Unit,
    focusCountryCode: String?,
    onFocusConsumed: () -> Unit,
    viewModel: CountriesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(-15.0, -60.0), // centro da America do Sul
            2f
        )
    }

    CountriesListInputEffects(onAction = viewModel::onAction)

    LaunchedEffect(focusCountryCode) {
        val code = focusCountryCode ?: return@LaunchedEffect
        if (code.isBlank()) return@LaunchedEffect

        viewModel.onAction(CountriesListAction.FocusCountryFromNavigation(code))
        onFocusConsumed()
    }

    CountriesListEventEffects(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
        cameraPositionState = cameraPositionState,
        onNavigateToCountryDetails = onNavigateToCountryDetails
    )

    CountriesListScreen(
        uiState = uiState,
        cameraPositionState = cameraPositionState,
        onAction = viewModel::onAction,
    )

    SnackbarHost(hostState = snackbarHostState)
}
