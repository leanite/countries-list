package com.github.leanite.countries.feature.list

import androidx.compose.ui.test.junit4.createComposeRule
import com.github.leanite.countries.feature.list.fixture.brazilCountryForTest
import com.github.leanite.countries.feature.list.robot.countriesListRobot
import com.github.leanite.countries.feature.list.ui.list.CountryItem
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CountryItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCountryName() {
        composeTestRule.setContent {
            CountryItem(
                country = brazilCountryForTest,
                onClick = {},
                onSeeMoreClick = {}
            )
        }

        countriesListRobot(composeTestRule) assert {
            countryIsVisible("Brasil")
            seeMoreIsDisplayed()
        }
    }

    @Test
    fun shouldTriggerOnClickWhenTapped() {
        var clicked = false
        composeTestRule.setContent {
            CountryItem(
                country = brazilCountryForTest,
                onClick = { clicked = true },
                onSeeMoreClick = {}
            )
        }

        countriesListRobot(composeTestRule) {
            clickCountry("Brasil")
        }

        assertTrue(clicked)
    }

    @Test
    fun shouldTriggerOnSeeMoreClickWhenTapped() {
        var seeMoreClicked = false
        composeTestRule.setContent {
            CountryItem(
                country = brazilCountryForTest,
                onClick = {},
                onSeeMoreClick = { seeMoreClicked = true }
            )
        }

        countriesListRobot(composeTestRule) {
            clickSeeMore()
        }

        assertTrue(seeMoreClicked)
    }
}
