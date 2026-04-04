package com.github.leanite.countries.feature.list.fixture

import com.github.leanite.countries.feature.list.ui.model.CountryLetterIndex
import com.github.leanite.countries.feature.list.ui.model.CountrySection
import com.github.leanite.countries.feature.list.ui.model.CountrySectionList

val emptySectionsForTest = CountrySectionList(emptyList(), emptyList())

val singleBrazilSectionForTest = CountrySectionList(
    sections = listOf(
        CountrySection('B', listOf(brazilCountryForTest)),
    ),
    index = listOf(
        CountryLetterIndex('B', 0),
    )
)

val sectionsForTest = CountrySectionList(
    sections = listOf(
        CountrySection('A', listOf(argentinaCountryForTest, germanyCountryForTest)),
        CountrySection('B', listOf(brazilCountryForTest)),
    ),
    index = listOf(
        CountryLetterIndex('A', 0),
        CountryLetterIndex('B', 3),
    )
)
