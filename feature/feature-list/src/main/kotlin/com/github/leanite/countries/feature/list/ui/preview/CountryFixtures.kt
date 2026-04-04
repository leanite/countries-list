package com.github.leanite.countries.feature.list.ui.preview

import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryCode
import com.github.leanite.countries.feature.list.ui.model.CountryLetterIndex
import com.github.leanite.countries.feature.list.ui.model.CountrySection
import com.github.leanite.countries.feature.list.ui.model.CountrySectionList
import kotlinx.collections.immutable.persistentListOf

val countryFixture = Country(
    code = CountryCode("BRA"),
    name = "Brasil", flagUrl = "brasil.svg",
    location = null,
    area = 8515767.0
)

val countriesPreviewFixture = persistentListOf(
    Country(code = CountryCode("ARG"), name = "Argentina", area = 2780400.0),
    Country(code = CountryCode("BRA"), name = "Brasil", area = 8515767.0),
    Country(code = CountryCode("CHL"), name = "Chile", area = 756102.0),
)

val sectionsPreviewFixture = CountrySectionList(
    sections = listOf(
        CountrySection('A', listOf(countriesPreviewFixture[0])),
        CountrySection('B', listOf(countriesPreviewFixture[1])),
        CountrySection('C', listOf(countriesPreviewFixture[2])),
    ),
    index = listOf(
        CountryLetterIndex('A', 0),
        CountryLetterIndex('B', 2),
        CountryLetterIndex('C', 4),
    )
)