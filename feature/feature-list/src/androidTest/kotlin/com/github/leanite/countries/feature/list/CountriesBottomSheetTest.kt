package com.github.leanite.countries.feature.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.leanite.countries.feature.list.fixture.brazilCountryForTest
import com.github.leanite.countries.feature.list.fixture.errorListUiState
import com.github.leanite.countries.feature.list.fixture.loadingListUiState
import com.github.leanite.countries.feature.list.fixture.singleBrazilSectionForTest
import com.github.leanite.countries.feature.list.fixture.successListUiState
import com.github.leanite.countries.feature.list.robot.countriesListRobot
import com.github.leanite.countries.feature.list.ui.CountriesBottomSheet
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CountriesBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val capturedActions = mutableListOf<CountriesListAction>()

    private fun setContent(uiState: CountriesListUiState) {
        composeTestRule.setContent {
            Box(Modifier.fillMaxSize()) {
                CountriesBottomSheet(
                    uiState = uiState,
                    onAction = { capturedActions.add(it) }
                )
            }
        }
    }

    @Test
    fun shouldShowLoadingWhenStateIsLoading() {
        setContent(loadingListUiState)

        countriesListRobot(composeTestRule) assert {
            loadingIsDisplayed()
        }
    }

    @Test
    fun shouldShowErrorMessageWhenStateIsError() {
        setContent(errorListUiState)

        countriesListRobot(composeTestRule) assert {
            textIsDisplayed("Não foi possível carregar os dados.")
        }
    }

    @Test
    fun shouldShowCountriesWhenStateIsSuccess() {
        setContent(successListUiState)

        countriesListRobot(composeTestRule) assert {
            countryIsVisible("Argentina")
            countryIsVisible("Brasil")
            countryIsVisible("Alemanha")
        }
    }

    @Test
    fun shouldDispatchRetryWhenErrorTapped() {
        setContent(errorListUiState)

        countriesListRobot(composeTestRule) {
            clickRetry()
        } assert {
            actionCaptured<CountriesListAction.Retry>(capturedActions)
        }
    }

    @Test
    fun shouldDispatchCountryClickWhenCountryTapped() {
        setContent(successListUiState)

        countriesListRobot(composeTestRule) {
            clickCountry("Brasil")
        } assert {
            actionCaptured<CountriesListAction.CountryClick>(capturedActions) {
                it.country.code?.value == "BRA"
            }
        }
    }

    @Test
    fun shouldDispatchCountryDetailsClickWhenSeeMoreTapped() {
        setContent(
            successListUiState.copy(
                visibleCountries = listOf(brazilCountryForTest),
                countrySections = singleBrazilSectionForTest
            )
        )

        countriesListRobot(composeTestRule) {
            clickSeeMore()
        } assert {
            actionCaptured<CountriesListAction.CountryDetailsClick>(capturedActions)
        }
    }

    @Test
    fun shouldDispatchSearchQueryChangeWhenTyping() {
        setContent(successListUiState)

        countriesListRobot(composeTestRule) {
            typeSearchQuery("Bra")
        } assert {
            actionCaptured<CountriesListAction.SearchQueryChange>(capturedActions) {
                it.query.contains("Bra")
            }
        }
    }

    @Test
    fun shouldDispatchRegionFilterChangeWhenFilterSelected() {
        setContent(successListUiState)

        countriesListRobot(composeTestRule) {
            selectRegionFilter("Américas")
        } assert {
            actionCaptured<CountriesListAction.RegionFilterChange>(capturedActions) {
                it.regionKey == "Americas"
            }
        }
    }
}
