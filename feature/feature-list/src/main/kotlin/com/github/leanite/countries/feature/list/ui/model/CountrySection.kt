package com.github.leanite.countries.feature.list.ui.model

import androidx.compose.runtime.Immutable
import com.github.leanite.countries.core.domain.model.Country

@Immutable
data class CountrySection(
    val letter: Char,
    val countries: List<Country>
)

@Immutable
data class CountryLetterIndex(
    val letter: Char,
    val firstItemPosition: Int
)

@Immutable
data class CountrySectionList(
    val sections: List<CountrySection>,
    val index: List<CountryLetterIndex>
)
