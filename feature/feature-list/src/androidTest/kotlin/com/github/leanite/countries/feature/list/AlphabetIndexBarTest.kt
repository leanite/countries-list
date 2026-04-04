package com.github.leanite.countries.feature.list

import androidx.compose.ui.test.junit4.createComposeRule
import com.github.leanite.countries.feature.list.robot.countriesListRobot
import com.github.leanite.countries.feature.list.ui.list.AlphabetIndexBar
import org.junit.Rule
import org.junit.Test

class AlphabetIndexBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldEnableLetterThatHasCountries() {
        composeTestRule.setContent {
            AlphabetIndexBar(
                indexByLetter = mapOf('A' to 0, 'B' to 5),
                onLetterSelected = {}
            )
        }

        countriesListRobot(composeTestRule) assert {
            letterIsEnabled("A")
            letterIsEnabled("B")
        }
    }

    @Test
    fun shouldDisableLetterWithNoCountries() {
        composeTestRule.setContent {
            AlphabetIndexBar(
                indexByLetter = mapOf('A' to 0),
                onLetterSelected = {}
            )
        }

        countriesListRobot(composeTestRule) assert {
            letterIsDisabled("Z")
            letterIsDisabled("#")
        }
    }
}
