package com.github.leanite.countries.feature.details

import androidx.compose.ui.test.junit4.createComposeRule
import com.github.leanite.countries.feature.details.fixture.errorUiState
import com.github.leanite.countries.feature.details.fixture.loadingUiState
import com.github.leanite.countries.feature.details.fixture.successUiState
import com.github.leanite.countries.feature.details.fixture.successWithoutBordersUiState
import com.github.leanite.countries.feature.details.robot.countryDetailsRobot
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CountryDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val capturedActions = mutableListOf<CountryDetailsAction>()

    private fun setContent(uiState: CountryDetailsUiState) {
        composeTestRule.setContent {
            CountryDetailsScreen(
                uiState = uiState,
                onAction = { capturedActions.add(it) },
                onBackClick = {}
            )
        }
    }

    @Test
    fun shouldShowLoadingWhenStateIsLoading() {
        setContent(loadingUiState)

        countryDetailsRobot(composeTestRule) assert {
            loadingIsDisplayed()
        }
    }

    @Test
    fun shouldShowErrorAndRetryWhenStateIsError() {
        setContent(errorUiState)

        countryDetailsRobot(composeTestRule) assert {
            errorMessageIsDisplayed()
            retryButtonIsDisplayed()
        }
    }

    @Test
    fun shouldShowContentWhenStateIsSuccess() {
        setContent(successUiState)

        countryDetailsRobot(composeTestRule) assert {
            textIsDisplayed("República Federativa do Brasil")
        }
    }

    @Test
    fun shouldShowBordersWhenAvailable() {
        setContent(successUiState)

        countryDetailsRobot(composeTestRule) {
            scrollToText("Fronteiras")
            scrollToText("Argentina")

        } assert {
            textExists("Fronteiras")
            textExists("Argentina")
        }
    }

    @Test
    fun shouldNotShowBordersWhenUnavailable() {
        setContent(successWithoutBordersUiState)

        countryDetailsRobot(composeTestRule) assert {
            textDoesNotExist("Fronteiras")
        }
    }

    @Test
    fun shouldDispatchRetryWhenRetryClicked() {
        setContent(errorUiState)

        countryDetailsRobot(composeTestRule) {
            clickRetry()
        }

        assertTrue(capturedActions.any { it is CountryDetailsAction.Retry })
    }

    @Test
    fun shouldDispatchSeeOnMapWhenMapIconClicked() {
        setContent(successUiState)

        countryDetailsRobot(composeTestRule) {
            clickSeeOnMap()
        } assert {
            actionCaptured<CountryDetailsAction.SeeOnMapClick>(capturedActions)
        }
    }

    @Test
    fun shouldDispatchBorderCountryClickWhenBorderTapped() {
        setContent(successUiState)

        countryDetailsRobot(composeTestRule) {
            scrollToText("Argentina")
            clickBorderCountry("Argentina")
        } assert {
            actionCaptured<CountryDetailsAction.BorderCountryClick>(capturedActions){
                it.countryCode == "ARG"
            }
        }
    }
}
