package com.github.leanite.countries.feature.list.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.feature.list.ui.model.CountrySectionList
import com.github.leanite.countries.feature.list.ui.preview.countriesPreviewFixture
import com.github.leanite.countries.feature.list.ui.preview.sectionsPreviewFixture
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Composable
fun CountryList(
    visibleCountries: ImmutableList<Country>,
    sectionList: CountrySectionList,
    showSectionHeaders: Boolean,
    showAlphabetBar: Boolean,
    listContentAlpha: Float,
    listState: LazyListState,
    onCountryClick: (Country) -> Unit,
    onCountryDetailsClick: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val hasIndex = showAlphabetBar && sectionList.index.isNotEmpty()
    val indexByLetter = sectionList.index.associate { it.letter to it.firstItemPosition }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = if (hasIndex) 24.dp else 0.dp)
                .graphicsLayer { alpha = listContentAlpha }
        ) {
            if (showSectionHeaders) {
                sectionList.sections.forEach { section ->
                    item(key = "header-${section.letter}") {
                        CountrySectionHeader(letter = section.letter)
                    }
                    items(
                        items = section.countries,
                        key = { it.code?.value ?: it.name.orEmpty() }
                    ) { country ->
                        CountryItem(
                            country = country,
                            onClick = { onCountryClick(country) },
                            onSeeMoreClick = { onCountryDetailsClick(country) }
                        )
                    }
                }
            } else {
                items(
                    items = visibleCountries,
                    key = { it.code?.value ?: it.name.orEmpty() }
                ) { country ->
                    CountryItem(
                        country = country,
                        onClick = { onCountryClick(country) },
                        onSeeMoreClick = { onCountryDetailsClick(country) }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 2.dp, top = 6.dp, bottom = 6.dp)
        ) {
            AnimatedVisibility(
                visible = hasIndex,
                enter = fadeIn(tween(180)) + slideInHorizontally(
                    animationSpec = tween(180),
                    initialOffsetX = { it / 2 }
                ),
                exit = fadeOut(tween(120)) + slideOutHorizontally(
                    animationSpec = tween(120),
                    targetOffsetX = { it / 2 }
                )
            ) {
                AlphabetIndexBar(
                    indexByLetter = indexByLetter,
                    onLetterSelected = { itemPosition ->
                        scope.launch { listState.scrollToItem(itemPosition) }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 2.dp, top = 6.dp, bottom = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun CountryListPreview() {
    CountryList(
        visibleCountries = countriesPreviewFixture,
        sectionList = sectionsPreviewFixture,
        showSectionHeaders = true,
        showAlphabetBar = true,
        listContentAlpha = 1f,
        listState = rememberLazyListState(),
        onCountryClick = {},
        onCountryDetailsClick = {}
    )
}

internal fun findCountryItemPosition(
    countryCode: String,
    visibleCountries: List<Country>,
    showSectionHeaders: Boolean,
    sectionList: CountrySectionList
): Int? {
    if (!showSectionHeaders) {
        val index = visibleCountries.indexOfFirst {
            it.code?.value.equals(countryCode, ignoreCase = true)
        }
        return index.takeIf { it >= 0 }
    }

    var position = 0
    sectionList.sections.forEach { section ->
        position += 1
        val countryIndex = section.countries.indexOfFirst {
            it.code?.value.equals(countryCode, ignoreCase = true)
        }

        if (countryIndex >= 0) {
            return position + countryIndex
        }

        position += section.countries.size
    }

    return null
}
