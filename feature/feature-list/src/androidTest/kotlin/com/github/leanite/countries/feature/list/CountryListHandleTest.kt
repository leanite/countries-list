package com.github.leanite.countries.feature.list

import androidx.compose.ui.test.junit4.createComposeRule
import com.github.leanite.countries.feature.list.robot.countriesListRobot
import com.github.leanite.countries.feature.list.ui.list.CountryListHandle
import com.github.leanite.countries.feature.list.ui.model.RegionFilter
import kotlinx.collections.immutable.toImmutableList
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CountryListHandleTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val filters = listOf(
        RegionFilter("ALL", "A-Z"),
        RegionFilter("Americas", "Américas"),
        RegionFilter("Europe", "Europa"),
    ).toImmutableList()

    private var capturedQuery = ""
    private var capturedFilter = ""

    private fun setContent(
        searchQuery: String = "",
        selectedRegionFilter: String = "ALL"
    ) {
        composeTestRule.setContent {
            CountryListHandle(
                searchQuery = searchQuery,
                onSearchQueryChanged = { capturedQuery = it },
                regionFilters = filters,
                selectedRegionFilter = selectedRegionFilter,
                onRegionFilterSelected = { capturedFilter = it }
            )
        }
    }

    @Test
    fun shouldDisplaySearchField() {
        setContent()

        countriesListRobot(composeTestRule) assert {
            searchFieldIsDisplayed()
        }
    }

    @Test
    fun shouldDisplayRegionFilters() {
        setContent()

        countriesListRobot(composeTestRule) assert {
            filterIsDisplayed("A-Z")
            filterIsDisplayed("Américas")
            filterIsDisplayed("Europa")
        }
    }

    @Test
    fun shouldCallOnSearchQueryChangedWhenTyping() {
        setContent()

        countriesListRobot(composeTestRule) {
            typeSearchQuery("Bra")
        }

        assertEquals("Bra", capturedQuery)
    }

    @Test
    fun shouldCallOnRegionFilterSelectedWhenChipClicked() {
        setContent()

        countriesListRobot(composeTestRule) {
            selectRegionFilter("Américas")
        }

        assertEquals("Americas", capturedFilter)
    }

    @Test
    fun shouldShowSelectedFilterAsSelected() {
        setContent(selectedRegionFilter = "Americas")

        countriesListRobot(composeTestRule) assert {
            filterIsSelected("Américas")
        }
    }
}
