package com.github.leanite.countries.feature.list.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.leanite.countries.core.bottomsheet.BottomSheetScrollMode
import com.github.leanite.countries.core.bottomsheet.BottomSheetState
import com.github.leanite.countries.core.bottomsheet.DraggableBottomSheet
import com.github.leanite.countries.core.bottomsheet.rememberDraggableBottomSheetState
import com.github.leanite.countries.feature.list.ui.model.ALL_REGION_FILTER_KEY
import com.github.leanite.countries.feature.list.CountriesListAction
import com.github.leanite.countries.feature.list.CountriesListContentState
import com.github.leanite.countries.feature.list.CountriesListUiState
import com.github.leanite.countries.feature.list.R
import com.github.leanite.countries.feature.list.ui.list.CountryList
import com.github.leanite.countries.feature.list.ui.list.CountryListHandle
import com.github.leanite.countries.feature.list.ui.list.CountryListTransitionEffect
import com.github.leanite.countries.feature.list.ui.preview.countriesPreviewFixture
import com.github.leanite.countries.feature.list.ui.preview.sectionsPreviewFixture
import kotlinx.collections.immutable.toImmutableList

@Composable
fun BoxScope.CountriesBottomSheet(
    uiState: CountriesListUiState,
    onAction: (CountriesListAction) -> Unit,
) {
    val bottomSheetState = rememberDraggableBottomSheetState(
        initialState = uiState.bottomSheetState,
        onSheetStateChanged = { onAction(CountriesListAction.BottomSheetStateChange(it)) },
        scrollMode = BottomSheetScrollMode.HandleOnly
    )

    DraggableBottomSheet(
        state = bottomSheetState,
        modifier = Modifier.align(Alignment.BottomCenter),
        handleContent = {
            CountryListHandle(
                searchQuery = uiState.searchQuery,
                onSearchQueryChanged = { onAction(CountriesListAction.SearchQueryChange(it)) },
                regionFilters = uiState.regionFilters.toImmutableList(),
                selectedRegionFilter = uiState.selectedRegionFilter,
                onRegionFilterSelected = { onAction(CountriesListAction.RegionFilterChange(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp)
            )
        }
    ) { contentState ->
        val showSectionHeaders =
            uiState.searchQuery.isBlank() && uiState.selectedRegionFilter == ALL_REGION_FILTER_KEY
        val showAlphabetBar = showSectionHeaders &&
                contentState.sheetState != BottomSheetState.Collapsed
        val isLoading = uiState.contentState == CountriesListContentState.Loading
        val isError = uiState.contentState == CountriesListContentState.Error

        when {
            isLoading && uiState.visibleCountries.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .testTag(CountriesBottomSheetTestTags.LOADING),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            isError && uiState.visibleCountries.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.error_load_failed),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { onAction(CountriesListAction.Retry) },
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
            else -> {
                val displayState = CountryListTransitionEffect(
                    visibleCountries = uiState.visibleCountries.toImmutableList(),
                    sectionList = uiState.countrySections,
                    showSectionHeaders = showSectionHeaders,
                    selectedRegionFilter = uiState.selectedRegionFilter,
                    focusCountryCode = uiState.focusCountryCode,
                    onFocusCountryHandled = { onAction(CountriesListAction.FocusCountryHandled) },
                    listState = contentState.listState,
                )

                CountryList(
                    visibleCountries = displayState.countries,
                    sectionList = displayState.sections,
                    showSectionHeaders = displayState.showHeaders,
                    showAlphabetBar = showAlphabetBar,
                    listContentAlpha = displayState.alpha,
                    listState = contentState.listState,
                    onCountryClick = { onAction(CountriesListAction.CountryClick(it)) },
                    onCountryDetailsClick = { onAction(CountriesListAction.CountryDetailsClick(it)) },
                )
            }
        }
    }
}

object CountriesBottomSheetTestTags {
    const val LOADING = "countries_loading"
}

@Preview(showBackground = true, heightDp = 600)
@Composable
private fun CountriesBottomSheetLoadingPreview() {
    Box(Modifier.fillMaxSize()) {
        CountriesBottomSheet(
            uiState = CountriesListUiState(contentState = CountriesListContentState.Loading),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 600)
@Composable
private fun CountriesBottomSheetErrorPreview() {
    Box(Modifier.fillMaxSize()) {
        CountriesBottomSheet(
            uiState = CountriesListUiState(contentState = CountriesListContentState.Error),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 600)
@Composable
private fun CountriesBottomSheetSuccessPreview() {
    Box(Modifier.fillMaxSize()) {
        CountriesBottomSheet(
            uiState = CountriesListUiState(
                contentState = CountriesListContentState.Success,
                visibleCountries = countriesPreviewFixture,
                countrySections = sectionsPreviewFixture
            ),
            onAction = {}
        )
    }
}
