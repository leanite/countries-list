package com.github.leanite.countries.feature.list

import androidx.compose.runtime.Immutable
import com.github.leanite.countries.core.bottomsheet.BottomSheetState
import com.github.leanite.countries.core.domain.result.AppError
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.Location
import com.github.leanite.countries.feature.list.ui.model.ALL_REGION_FILTER_KEY
import com.github.leanite.countries.feature.list.ui.model.CountrySectionList
import com.github.leanite.countries.feature.list.ui.model.RegionFilter
import com.github.leanite.countries.feature.list.ui.model.defaultRegionFilters

@Immutable
data class CountriesListUiState(
    val contentState: CountriesListContentState = CountriesListContentState.Idle,
    val bottomSheetState: BottomSheetState = BottomSheetState.Collapsed,
    val visibleCountries: List<Country> = emptyList(),
    val countrySections: CountrySectionList = CountrySectionList(emptyList(), emptyList()),
    val selectedCountry: Country? = null,
    val searchQuery: String = "",
    val regionFilters: List<RegionFilter> = defaultRegionFilters,
    val selectedRegionFilter: String = ALL_REGION_FILTER_KEY,
    val focusCountryCode: String? = null
) {/**/
    val isMapNavigationEnabled: Boolean
        get() = bottomSheetState == BottomSheetState.Collapsed
}

@Immutable
sealed interface CountriesListContentState {
    data object Idle : CountriesListContentState
    data object Loading : CountriesListContentState
    data object Success : CountriesListContentState
    data object Error : CountriesListContentState
}

@Immutable
sealed interface CountriesListAction {
    data object Load : CountriesListAction
    data object Retry : CountriesListAction
    data class BottomSheetStateChange(val bottomSheetState: BottomSheetState) : CountriesListAction
    data class SearchQueryChange(val query: String) : CountriesListAction
    data class CountryClick(val country: Country) : CountriesListAction
    data class CountryDetailsClick(val country: Country) : CountriesListAction
    data class RegionFilterChange(val regionKey: String) : CountriesListAction
    data class FocusCountryFromNavigation(val countryCode: String) : CountriesListAction
    data object FocusCountryHandled : CountriesListAction
}

@Immutable
sealed interface CountriesListMessage {
    data object CountryNotFound : CountriesListMessage
    data class LocationUnavailable(val countryName: String) : CountriesListMessage
    data object CountryCodeUnavailable : CountriesListMessage
    data class ApiError(val error: AppError) : CountriesListMessage
}

@Immutable
sealed interface CountriesListEvent {
    data class ShowMessage(val type: CountriesListMessage) : CountriesListEvent
    data class MoveCameraToLocation(
        val location: Location, val targetZoom: Float,
        val fullAnimation: Boolean = true
    ) : CountriesListEvent
    data class NavigateToCountryDetails(val countryCode: String) : CountriesListEvent
}
