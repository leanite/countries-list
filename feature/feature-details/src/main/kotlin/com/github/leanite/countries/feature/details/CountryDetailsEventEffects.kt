package com.github.leanite.countries.feature.details

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.github.leanite.countries.core.domain.result.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CountryDetailsEventEffects(
    events: Flow<CountryDetailsEvent>,
    snackbarHostState: SnackbarHostState,
    onNavigateToCountryDetails: (String) -> Unit,
    onOpenOnMap: (String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(events, snackbarHostState) {
        events.collectLatest { event ->
            when (event) {
                is CountryDetailsEvent.ShowMessage -> {
                    val message = event.type.resolve(context)
                    snackbarHostState.showSnackbar(message)
                }
                is CountryDetailsEvent.OpenOnMap -> onOpenOnMap(event.countryCode)
                is CountryDetailsEvent.NavigateToCountryDetails -> onNavigateToCountryDetails(event.countryCode)
            }
        }
    }
}

private fun CountryDetailsMessage.resolve(context: Context): String = when (this) {
    is CountryDetailsMessage.BorderCodeUnavailable ->
        context.getString(R.string.error_border_code_unavailable)
    is CountryDetailsMessage.CountryCodeUnavailable ->
        context.getString(R.string.error_country_code_unavailable)
    is CountryDetailsMessage.BorderLoadFailed ->
        context.getString(R.string.error_border_load_failed)
    is CountryDetailsMessage.ApiError -> when (error) {
        AppError.NoInternet -> context.getString(R.string.error_no_internet)
        AppError.Unauthorized -> context.getString(R.string.error_unauthorized)
        AppError.ServerError -> context.getString(R.string.error_server)
        AppError.NotFound -> context.getString(R.string.error_not_found)
        AppError.InvalidData -> context.getString(R.string.error_invalid_data)
        AppError.Unknown -> context.getString(R.string.error_unknown)
    }
}
