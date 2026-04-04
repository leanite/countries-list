package com.github.leanite.countries.feature.list.fixture

import com.github.leanite.countries.core.bottomsheet.BottomSheetState
import com.github.leanite.countries.feature.list.CountriesListContentState
import com.github.leanite.countries.feature.list.CountriesListUiState

val loadingListUiState = CountriesListUiState(
    contentState = CountriesListContentState.Loading
)

val errorListUiState = CountriesListUiState(
    contentState = CountriesListContentState.Error
)

val successListUiState = CountriesListUiState(
    contentState = CountriesListContentState.Success,
    bottomSheetState = BottomSheetState.Half,
    visibleCountries = allCountriesForTest,
    countrySections = sectionsForTest
)
