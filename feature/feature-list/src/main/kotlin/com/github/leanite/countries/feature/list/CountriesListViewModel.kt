package com.github.leanite.countries.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.leanite.countries.core.bottomsheet.BottomSheetState
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.result.AppResult
import com.github.leanite.countries.feature.list.ui.model.ALL_REGION_FILTER_KEY
import com.github.leanite.countries.feature.list.ui.model.CountrySectionList
import com.github.leanite.countries.feature.list.usecase.BuildCountrySectionsUseCase
import com.github.leanite.countries.feature.list.usecase.CalculateCountryTargetZoomUseCase
import com.github.leanite.countries.feature.list.usecase.GetAllCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountriesListViewModel @Inject constructor(
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
    private val buildCountrySectionsUseCase: BuildCountrySectionsUseCase,
    private val calculateCountryTargetZoomUseCase: CalculateCountryTargetZoomUseCase
) : ViewModel() {
    private var allCountries: List<Country> = emptyList()

    private val _uiState = MutableStateFlow<CountriesListUiState>(CountriesListUiState())
    val uiState: StateFlow<CountriesListUiState> = _uiState.asStateFlow()

    private val _events = Channel<CountriesListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAction(action: CountriesListAction) {
        when (action) {
            CountriesListAction.Load -> loadCountries()
            CountriesListAction.Retry -> loadCountries(force = true)
            is CountriesListAction.BottomSheetStateChange -> onBottomSheetStateChange(action.bottomSheetState)
            is CountriesListAction.SearchQueryChange -> onSearchQueryChange(action.query)
            is CountriesListAction.CountryClick -> onCountryClick(action.country)
            is CountriesListAction.CountryDetailsClick -> onCountryDetailsClick(action.country)
            is CountriesListAction.RegionFilterChange -> onRegionFilterChange(action.regionKey)
            is CountriesListAction.FocusCountryFromNavigation -> onFocusCountryFromNavigation(action.countryCode)
            CountriesListAction.FocusCountryHandled -> onFocusCountryHandled()
        }
    }

    private fun loadCountries(force: Boolean = false) {
        val state = _uiState.value.contentState

        if (state == CountriesListContentState.Loading) return
        if (!force && state == CountriesListContentState.Success) return

        viewModelScope.launch {
            _uiState.update { it.copy(contentState = CountriesListContentState.Loading) }

            when (val result = getAllCountriesUseCase()) {
                is AppResult.Success -> {
                    allCountries = result.data
                    _uiState.update {
                        it.copy(
                            contentState = CountriesListContentState.Success,
                        ).withFilteredCountries()
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(contentState = CountriesListContentState.Error) }
                    showMessage(CountriesListMessage.ApiError(result.error))
                }
            }
        }
    }

    private fun onCountryClick(country: Country) {
        focusCountry(country = country, focusCode = null)
    }

    private fun onFocusCountryFromNavigation(countryCode: String) {
        val normalizedCode = countryCode.trim()
        if (normalizedCode.isBlank()) return
        if (allCountries.isEmpty()) return

        clearSearch()

        val targetCountry = findCountryByCode(normalizedCode)
        if (targetCountry == null) {
            showMessage(CountriesListMessage.CountryNotFound)
            return
        }

        resetFilterIfCountryNotVisible(targetCountry)

        focusCountry(
            country = targetCountry,
            focusCode = targetCountry.code?.value ?: normalizedCode,
            fullAnimation = false
        )
    }

    private fun resetFilterIfCountryNotVisible(country: Country) {
        val currentFilter = _uiState.value.selectedRegionFilter
        if (currentFilter == ALL_REGION_FILTER_KEY) return

        val countryMatchesFilter = country.region.equals(currentFilter, ignoreCase = true)
        if (!countryMatchesFilter) {
            _uiState.update {
                it.copy(selectedRegionFilter = ALL_REGION_FILTER_KEY).withFilteredCountries()
            }
        }
    }

    private fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "").withFilteredCountries() }
    }

    private fun findCountryByCode(code: String): Country? {
        return allCountries.firstOrNull {
            it.code?.value.equals(code, ignoreCase = true)
        }
    }

    private fun focusCountry(country: Country, focusCode: String?, fullAnimation: Boolean = true) {
        _uiState.update {
            it.copy(
                selectedCountry = country,
                focusCountryCode = focusCode
            )
        }
        moveCameraToCountry(country, fullAnimation)
    }

    private fun moveCameraToCountry(country: Country, fullAnimation: Boolean = true) {
        val location = country.location
        if (location == null) {
            showMessage(CountriesListMessage.LocationUnavailable(country.name ?: "este país"))
            return
        }

        viewModelScope.launch {
            val targetZoom = calculateCountryTargetZoomUseCase(country.area)
            _events.send(CountriesListEvent.MoveCameraToLocation(location, targetZoom, fullAnimation))
        }
    }

    private fun onCountryDetailsClick(country: Country) {
        val countryCode = country.code?.value
        if (countryCode.isNullOrBlank()) {
            showMessage(CountriesListMessage.CountryCodeUnavailable)
            return
        }

        viewModelScope.launch {
            _events.send(CountriesListEvent.NavigateToCountryDetails(countryCode))
        }
    }

    private fun onRegionFilterChange(region: String) {
        val state = _uiState.value
        if (region == state.selectedRegionFilter) return

        _uiState.update {
            it.copy(selectedRegionFilter = region).withFilteredCountries()
        }
    }

    private fun onBottomSheetStateChange(state: BottomSheetState) {
        _uiState.update { it.copy(bottomSheetState = state) }
    }

    private fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query).withFilteredCountries() }
    }

    private fun onFocusCountryHandled() {
        _uiState.update { it.copy(focusCountryCode = null) }
    }

    private fun showMessage(type: CountriesListMessage) {
        viewModelScope.launch {
            _events.send(CountriesListEvent.ShowMessage(type))
        }
    }

    private fun CountriesListUiState.withFilteredCountries(): CountriesListUiState {
        val normalizedQuery = searchQuery.trim()

        val filteredByRegion =
            if (selectedRegionFilter == ALL_REGION_FILTER_KEY) {
                allCountries
            } else {
                allCountries.filter { country ->
                    country.region.equals(selectedRegionFilter, ignoreCase = true)
                }
            }

        val filteredCountries =
            if (normalizedQuery.isBlank()) {
                filteredByRegion
            } else {
                filteredByRegion.filter { country ->
                    country.name.orEmpty().contains(normalizedQuery, ignoreCase = true)
                }
            }

        val sections =
            if (normalizedQuery.isBlank() && selectedRegionFilter == ALL_REGION_FILTER_KEY) {
                buildCountrySectionsUseCase(filteredCountries)
            } else {
                CountrySectionList(emptyList(), emptyList())
            }

        return copy(
            visibleCountries = filteredCountries,
            countrySections = sections
        )
    }
}
