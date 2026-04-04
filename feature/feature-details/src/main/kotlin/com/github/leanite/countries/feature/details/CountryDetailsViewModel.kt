package com.github.leanite.countries.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.leanite.countries.core.domain.model.CountryDetails
import com.github.leanite.countries.core.domain.result.AppResult
import com.github.leanite.countries.core.domain.usecase.GetBorderCountriesUseCase
import com.github.leanite.countries.core.domain.usecase.GetCountryDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryDetailsViewModel @Inject constructor(
    private val getCountryDetailsUseCase: GetCountryDetailsUseCase,
    private val getBorderCountriesUseCase: GetBorderCountriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CountryDetailsUiState())
    val uiState: StateFlow<CountryDetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CountryDetailsEvent>()
    val events: SharedFlow<CountryDetailsEvent> = _events.asSharedFlow()

    fun onAction(action: CountryDetailsAction) {
        when (action) {
            is CountryDetailsAction.Load -> loadCountryDetails(action.countryCode)
            CountryDetailsAction.Retry -> retry()
            is CountryDetailsAction.BorderCountryClick -> onBorderCountryClick(action.countryCode)
            CountryDetailsAction.SeeOnMapClick -> onSeeOnMapClick()
        }
    }

    private fun onBorderCountryClick(countryCode: String?) {
        val normalizedCode = countryCode?.trim().orEmpty()
        if (normalizedCode.isBlank()) {
            viewModelScope.launch {
                _events.emit(CountryDetailsEvent.ShowMessage(CountryDetailsMessage.BorderCodeUnavailable))
            }
            return
        }

        viewModelScope.launch {
            _events.emit(CountryDetailsEvent.NavigateToCountryDetails(normalizedCode))
        }
    }

    private fun onSeeOnMapClick() {
        val countryCode = _uiState.value.countryCode
        if (countryCode.isBlank()) {
            viewModelScope.launch {
                _events.emit(CountryDetailsEvent.ShowMessage(CountryDetailsMessage.CountryCodeUnavailable))
            }
            return
        }

        viewModelScope.launch {
            _events.emit(CountryDetailsEvent.OpenOnMap(countryCode))
        }
    }

    private fun retry() {
        val countryCode = _uiState.value.countryCode
        if (countryCode.isBlank()) return
        loadCountryDetails(countryCode)
    }

    private fun loadCountryDetails(countryCode: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    contentState = CountryDetailsContentState.Loading,
                    countryCode = countryCode,
                    countryDetails = null
                )
            }

            when (val detailsResult = getCountryDetailsUseCase(countryCode)) {
                is AppResult.Success -> {
                    val detailsWithBorders = enrichBorderCountries(detailsResult.data)
                    _uiState.update {
                        it.copy(
                            contentState = CountryDetailsContentState.Success,
                            countryDetails = detailsWithBorders
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            contentState = CountryDetailsContentState.Error,
                            countryDetails = null
                        )
                    }
                    _events.emit(CountryDetailsEvent.ShowMessage(CountryDetailsMessage.ApiError(detailsResult.error)))
                }
            }
        }
    }

    private suspend fun enrichBorderCountries(countryDetails: CountryDetails): CountryDetails {
        val borderCodes = countryDetails.borders?.codes.orEmpty()
        if (borderCodes.isEmpty()) return countryDetails

        return when (val borderResult = getBorderCountriesUseCase(borderCodes)) {
            is AppResult.Success -> {
                countryDetails.copy(
                    borders = countryDetails.borders?.copy(countries = borderResult.data)
                )
            }

            is AppResult.Error -> {
                _events.emit(CountryDetailsEvent.ShowMessage(CountryDetailsMessage.BorderLoadFailed))
                countryDetails
            }
        }
    }
}