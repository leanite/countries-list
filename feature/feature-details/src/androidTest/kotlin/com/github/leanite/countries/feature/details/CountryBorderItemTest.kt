package com.github.leanite.countries.feature.details

import androidx.compose.ui.test.junit4.createComposeRule
import com.github.leanite.countries.feature.details.fixture.argentinaCountryForTest
import com.github.leanite.countries.feature.details.robot.countryDetailsRobot
import com.github.leanite.countries.feature.details.ui.CountryBorderItem
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CountryBorderItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCountryName() {
        composeTestRule.setContent {
            CountryBorderItem(country = argentinaCountryForTest, onClick = {})
        }

        countryDetailsRobot(composeTestRule) assert {
            countryNameIsDisplayed("Argentina")
        }
    }

    @Test
    fun shouldTriggerOnClickWhenTapped() {
        var clicked = false
        composeTestRule.setContent {
            CountryBorderItem(country = argentinaCountryForTest, onClick = { clicked = true })
        }

        countryDetailsRobot(composeTestRule) {
            clickBorderCountry("Argentina")
        }

        assertTrue(clicked)
    }
}
