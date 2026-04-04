package com.github.leanite.countries.feature.details.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.github.leanite.countries.feature.details.CountryDetailsAction
import com.github.leanite.countries.feature.details.CountryDetailsScreenTestTags
import org.junit.Assert.assertTrue

class CountryDetailsRobot(
    private val composeTestRule: ComposeTestRule
) {
    fun clickRetry() {
        composeTestRule
            .onNodeWithText("Tentar novamente")
            .performClick()
    }

    fun clickSeeOnMap() {
        composeTestRule
            .onNodeWithContentDescription("Ver no mapa")
            .performClick()
    }

    fun clickBorderCountry(name: String) {
        composeTestRule
            .onNodeWithText(name)
            .performClick()
    }

    fun scrollToText(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .performScrollTo()
    }

    infix fun assert(block: CountryDetailsAssert.() -> Unit): CountryDetailsAssert {
        return CountryDetailsAssert(composeTestRule).apply(block)
    }
}

class CountryDetailsAssert(
    private val composeTestRule: ComposeTestRule
) {
    fun loadingIsDisplayed() {
        composeTestRule
            .onNodeWithTag(CountryDetailsScreenTestTags.LOADING)
            .assertIsDisplayed()
    }

    fun errorMessageIsDisplayed() {
        composeTestRule
            .onNodeWithText("Não foi possível carregar os detalhes.")
            .assertIsDisplayed()
    }

    fun retryButtonIsDisplayed() {
        composeTestRule
            .onNodeWithText("Tentar novamente")
            .assertIsDisplayed()
    }

    fun textIsDisplayed(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .assertIsDisplayed()
    }

    fun textExists(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .assertExists()
    }

    fun textDoesNotExist(text: String) {
        composeTestRule
            .onNodeWithText(text)
            .assertDoesNotExist()
    }

    fun countryNameIsDisplayed(name: String) {
        composeTestRule
            .onNodeWithText(name)
            .assertIsDisplayed()
    }

    inline fun <reified T : CountryDetailsAction> actionCaptured(
        actions: List<CountryDetailsAction>,
        predicate: (T) -> Boolean = { true }
    ) {
        val filtered = actions.filterIsInstance<T>()
        assertTrue("No action of type ${T::class.simpleName} found", filtered.isNotEmpty())
        assertTrue("Action predicate failed", filtered.any { predicate(it) })
    }
}

fun countryDetailsRobot(
    composeTestRule: ComposeTestRule,
    block: CountryDetailsRobot.() -> Unit
): CountryDetailsRobot {
    return CountryDetailsRobot(composeTestRule).apply(block)
}

fun countryDetailsRobot(
    composeTestRule: ComposeTestRule
): CountryDetailsRobot {
    return CountryDetailsRobot(composeTestRule)
}
