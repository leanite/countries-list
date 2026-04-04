package com.github.leanite.countries.feature.list

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.github.leanite.countries.core.domain.result.AppError
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

private const val ZOOM_OUT_LEVEL = 2f
private const val STEP_DURATION_MS = 333

@Composable
fun CountriesListEventEffects(
    events: Flow<CountriesListEvent>,
    snackbarHostState: SnackbarHostState,
    cameraPositionState: CameraPositionState,
    onNavigateToCountryDetails: (String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(events, snackbarHostState) {
        events.collectLatest { event ->
            when (event) {
                is CountriesListEvent.ShowMessage -> {
                    val message = event.type.resolve(context)
                    snackbarHostState.showSnackbar(message)
                }
                is CountriesListEvent.NavigateToCountryDetails ->
                    onNavigateToCountryDetails(event.countryCode)
                is CountriesListEvent.MoveCameraToLocation -> {
                    val destination = LatLng(event.location.latitude, event.location.longitude)

                    if (!event.fullAnimation) {
                        partialAnimation(cameraPositionState, destination, event.targetZoom)
                    } else {
                        fullAnimation(cameraPositionState, destination, event.targetZoom)
                    }
                }
            }
        }
    }
}

private suspend fun partialAnimation(
    cameraPositionState: CameraPositionState, destination: LatLng, targetZoom: Float
) {
    // 2) desloca até o país mantendo zoom 2f
    cameraPositionState.animate(
        update = CameraUpdateFactory.newLatLngZoom(destination, ZOOM_OUT_LEVEL),
        durationMs = STEP_DURATION_MS
    )
    // 3) zoom in no país selecionado
    cameraPositionState.animate(
        update = CameraUpdateFactory.newLatLngZoom(destination, targetZoom),
        durationMs = STEP_DURATION_MS
    )
}

private suspend fun fullAnimation(
    cameraPositionState: CameraPositionState, destination: LatLng, targetZoom: Float
) {
    val currentTarget = cameraPositionState.position.target

    // 1) zoom out no ponto atual
    cameraPositionState.animate(
        update = CameraUpdateFactory.newLatLngZoom(currentTarget, ZOOM_OUT_LEVEL),
        durationMs = STEP_DURATION_MS
    )
    partialAnimation(cameraPositionState, destination, targetZoom)
}

private fun CountriesListMessage.resolve(context: Context): String = when (this) {
    is CountriesListMessage.CountryNotFound ->
        context.getString(R.string.error_country_not_found)
    is CountriesListMessage.LocationUnavailable ->
        context.getString(R.string.error_location_unavailable, countryName)
    is CountriesListMessage.CountryCodeUnavailable ->
        context.getString(R.string.error_country_code_unavailable)
    is CountriesListMessage.ApiError -> when (error) {
        AppError.NoInternet -> context.getString(R.string.error_no_internet)
        AppError.Unauthorized -> context.getString(R.string.error_unauthorized)
        AppError.ServerError -> context.getString(R.string.error_server)
        AppError.NotFound -> context.getString(R.string.error_not_found)
        AppError.InvalidData -> context.getString(R.string.error_invalid_data)
        AppError.Unknown -> context.getString(R.string.error_unknown)
    }
}
