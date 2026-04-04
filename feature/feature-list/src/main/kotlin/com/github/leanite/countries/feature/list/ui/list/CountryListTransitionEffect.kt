package com.github.leanite.countries.feature.list.ui.list

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.feature.list.ui.model.CountrySectionList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private const val LIST_FADE_IN_MS = 500

@Immutable
data class CountryListDisplayState(
    val countries: ImmutableList<Country>,
    val sections: CountrySectionList,
    val showHeaders: Boolean,
    val alpha: Float
)

@Composable
fun CountryListTransitionEffect(
    visibleCountries: ImmutableList<Country>,
    sectionList: CountrySectionList,
    showSectionHeaders: Boolean,
    selectedRegionFilter: String,
    focusCountryCode: String?,
    onFocusCountryHandled: () -> Unit,
    listState: LazyListState,
): CountryListDisplayState {
    val listContentAlpha = remember { Animatable(1f) }
    var previousRegionFilter by remember { mutableStateOf(selectedRegionFilter) }

    var displayedCountries by remember { mutableStateOf(visibleCountries) }
    var displayedSections by remember { mutableStateOf(sectionList) }
    var displayedShowHeaders by remember { mutableStateOf(showSectionHeaders) }

    // Scroll to focused country after navigating from CountryDetails
    LaunchedEffect(focusCountryCode) {
        val code = focusCountryCode ?: return@LaunchedEffect
        // If filter also changed, the filter LaunchedEffect handles the scroll
        if (previousRegionFilter != selectedRegionFilter) return@LaunchedEffect

        val targetPosition = findCountryItemPosition(
            countryCode = code,
            visibleCountries = displayedCountries,
            showSectionHeaders = displayedShowHeaders,
            sectionList = displayedSections
        )

        if (targetPosition != null) {
            listState.scrollToItem(targetPosition)
        }

        onFocusCountryHandled()
    }

    // Animated filter transition when filter changes
    LaunchedEffect(selectedRegionFilter) {
        if (previousRegionFilter == selectedRegionFilter) return@LaunchedEffect
        previousRegionFilter = selectedRegionFilter

        listContentAlpha.snapTo(0f)
        displayedCountries = visibleCountries
        displayedSections = sectionList
        displayedShowHeaders = showSectionHeaders

        val pendingFocusCode = focusCountryCode
        if (pendingFocusCode != null) {
            val targetPosition = findCountryItemPosition(
                countryCode = pendingFocusCode,
                visibleCountries = displayedCountries,
                showSectionHeaders = displayedShowHeaders,
                sectionList = displayedSections
            )
            listState.animateScrollToItem(targetPosition ?: 0)
            onFocusCountryHandled()
        } else {
            listState.animateScrollToItem(0)
        }

        listContentAlpha.animateTo(1f, tween(LIST_FADE_IN_MS))
    }

    // Sync displayed data when no list animation is running
    LaunchedEffect(visibleCountries, sectionList, showSectionHeaders) {
        if (listContentAlpha.value == 1f) {
            displayedCountries = visibleCountries
            displayedSections = sectionList
            displayedShowHeaders = showSectionHeaders
        }
    }

    return CountryListDisplayState(
        countries = displayedCountries,
        sections = displayedSections,
        showHeaders = displayedShowHeaders,
        alpha = listContentAlpha.asState().value
    )
}
