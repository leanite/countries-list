package com.github.leanite.countries.feature.list.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.leanite.countries.feature.list.CountriesListAction
import com.github.leanite.countries.feature.list.ui.CountriesBottomSheetTestTags
import org.junit.Assert.assertTrue

class CountriesListRobot(
    private val composeTestRule: ComposeTestRule
) {
    fun clickCountry(name: String) {
        composeTestRule.clickOnText(name)
    }

    fun clickSeeMore() {
        composeTestRule.clickOnText("Ver mais")
    }

    fun clickRetry() {
        composeTestRule.clickOnText("Tentar novamente")
    }

    fun typeSearchQuery(query: String) {
        composeTestRule.typeOnField("Buscar país", query)
    }

    fun clearSearch() {
        composeTestRule.clearField("Buscar país")
    }

    fun selectRegionFilter(filter: String) {
        composeTestRule.clickOnText(filter)
    }

    infix fun assert(block: CountriesListAssert.() -> Unit): CountriesListAssert {
        return CountriesListAssert(composeTestRule).apply(block)
    }
}

class CountriesListAssert(
    private val composeTestRule: ComposeTestRule
) {
    fun loadingIsDisplayed() {
        composeTestRule
            .onNodeWithTag(CountriesBottomSheetTestTags.LOADING)
            .assertIsDisplayed()
    }

    fun countryIsVisible(name: String) {
        composeTestRule.assertTextIsDisplayed(name)
    }

    fun sectionHeaderIsDisplayed(letter: String) {
        composeTestRule.assertTextIsDisplayed(letter)
    }

    fun textIsDisplayed(text: String) {
        composeTestRule.assertTextIsDisplayed(text)
    }

    fun seeMoreIsDisplayed() {
        composeTestRule.assertTextIsDisplayed("Ver mais")
    }

    fun searchFieldIsDisplayed() {
        composeTestRule.assertTextIsDisplayed("Buscar país")
    }

    fun filterIsDisplayed(displayName: String) {
        composeTestRule.assertTextIsDisplayed(displayName)
    }

    fun filterIsSelected(displayName: String) {
        composeTestRule
            .onNodeWithText(displayName)
            .assertIsSelected()
    }

    fun letterIsEnabled(letter: String) {
        composeTestRule
            .onNodeWithText(letter)
            .assertIsEnabled()
    }

    fun letterIsDisabled(letter: String) {
        composeTestRule
            .onNodeWithText(letter)
            .assertIsNotEnabled()
    }

    inline fun <reified T : CountriesListAction> actionCaptured(
        actions: List<CountriesListAction>,
        predicate: (T) -> Boolean = { true }
    ) {
        val filtered = actions.filterIsInstance<T>()
        assertTrue("No action of type ${T::class.simpleName} found", filtered.isNotEmpty())
        assertTrue("Action predicate failed", filtered.any { predicate(it) })
    }
}

fun countriesListRobot(
    composeTestRule: ComposeTestRule,
    block: CountriesListRobot.() -> Unit
): CountriesListRobot {
    return CountriesListRobot(composeTestRule).apply(block)
}

fun countriesListRobot(
    composeTestRule: ComposeTestRule
): CountriesListRobot {
    return CountriesListRobot(composeTestRule)
}
