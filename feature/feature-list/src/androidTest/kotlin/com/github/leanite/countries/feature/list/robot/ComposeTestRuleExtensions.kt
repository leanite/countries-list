package com.github.leanite.countries.feature.list.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput

internal fun ComposeTestRule.clickOnText(text: String) {
    onNodeWithText(text).performClick()
}

internal fun ComposeTestRule.typeOnField(label: String, query: String) {
    onNodeWithText(label).performTextInput(query)
}

internal fun ComposeTestRule.clearField(label: String) {
    onNodeWithText(label).performTextClearance()
}

internal fun ComposeTestRule.assertTextIsDisplayed(text: String) {
    onNodeWithText(text).assertIsDisplayed()
}

internal fun ComposeTestRule.assertTextExists(text: String) {
    onNodeWithText(text).assertExists()
}

internal fun ComposeTestRule.assertTextDoesNotExist(text: String) {
    onNodeWithText(text).assertDoesNotExist()
}
