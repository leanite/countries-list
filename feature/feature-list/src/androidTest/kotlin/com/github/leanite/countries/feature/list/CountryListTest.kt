package com.github.leanite.countries.feature.list

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.feature.list.fixture.allCountriesForTest
import com.github.leanite.countries.feature.list.fixture.brazilCountryForTest
import com.github.leanite.countries.feature.list.fixture.emptySectionsForTest
import com.github.leanite.countries.feature.list.fixture.sectionsForTest
import com.github.leanite.countries.feature.list.robot.countriesListRobot

import com.github.leanite.countries.feature.list.ui.list.CountryList
import com.github.leanite.countries.feature.list.ui.model.CountrySectionList
import kotlinx.collections.immutable.toImmutableList
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CountryListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val capturedClicks = mutableListOf<Country>()
    private val capturedDetailsClicks = mutableListOf<Country>()

    private fun setContentWithList(
        countries: List<Country> = allCountriesForTest,
        sectionList: CountrySectionList = emptySectionsForTest,
        showSectionHeaders: Boolean = false,
        showAlphabetBar: Boolean = false,
    ) {
        composeTestRule.setContent {
            CountryList(
                visibleCountries = countries.toImmutableList(),
                sectionList = sectionList,
                showSectionHeaders = showSectionHeaders,
                showAlphabetBar = showAlphabetBar,
                listContentAlpha = 1f,
                listState = rememberLazyListState(),
                onCountryClick = { capturedClicks.add(it) },
                onCountryDetailsClick = { capturedDetailsClicks.add(it) }
            )
        }
    }

    @Test
    fun shouldDisplayAllCountries() {
        setContentWithList()

        countriesListRobot(composeTestRule) assert {
            countryIsVisible("Argentina")
            countryIsVisible("Brasil")
            countryIsVisible("Alemanha")
        }
    }

    @Test
    fun shouldDisplaySectionHeaders() {
        setContentWithList(
            sectionList = sectionsForTest,
            showSectionHeaders = true
        )

        countriesListRobot(composeTestRule) assert {
            sectionHeaderIsDisplayed("A")
            sectionHeaderIsDisplayed("B")
            countryIsVisible("Argentina")
            countryIsVisible("Brasil")
        }
    }

    @Test
    fun shouldTriggerClickOnCountry() {
        setContentWithList()

        countriesListRobot(composeTestRule) {
            clickCountry("Brasil")
        }

        assertTrue(capturedClicks.any { it.code?.value == "BRA" })
    }

    @Test
    fun shouldTriggerSeeMoreClick() {
        setContentWithList(countries = listOf(brazilCountryForTest))

        countriesListRobot(composeTestRule) {
            clickSeeMore()
        }

        assertTrue(capturedDetailsClicks.any { it.code?.value == "BRA" })
    }
}
