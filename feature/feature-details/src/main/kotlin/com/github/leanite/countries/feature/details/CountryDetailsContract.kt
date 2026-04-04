package com.github.leanite.countries.feature.details

import androidx.compose.runtime.Immutable
import com.github.leanite.countries.core.domain.result.AppError
import com.github.leanite.countries.core.domain.model.CountryDetails

@Immutable
data class CountryDetailsUiState(
    val contentState: CountryDetailsContentState = CountryDetailsContentState.Idle,
    val countryCode: String = "",
    val countryDetails: CountryDetails? = null
)

@Immutable
sealed interface CountryDetailsContentState {
    data object Idle : CountryDetailsContentState
    data object Loading : CountryDetailsContentState
    data object Success : CountryDetailsContentState
    data object Error : CountryDetailsContentState
}

@Immutable
sealed interface CountryDetailsAction {
    data class Load(val countryCode: String) : CountryDetailsAction
    data object Retry : CountryDetailsAction
    data class BorderCountryClick(val countryCode: String?) : CountryDetailsAction
    data object SeeOnMapClick : CountryDetailsAction
}

@Immutable
sealed interface CountryDetailsMessage {
    data object BorderCodeUnavailable : CountryDetailsMessage
    data object CountryCodeUnavailable : CountryDetailsMessage
    data object BorderLoadFailed : CountryDetailsMessage
    data class ApiError(val error: AppError) : CountryDetailsMessage
}

@Immutable
sealed interface CountryDetailsEvent {
    data class ShowMessage(val type: CountryDetailsMessage) : CountryDetailsEvent
    data class NavigateToCountryDetails(val countryCode: String) : CountryDetailsEvent
    data class OpenOnMap(val countryCode: String) : CountryDetailsEvent
}
