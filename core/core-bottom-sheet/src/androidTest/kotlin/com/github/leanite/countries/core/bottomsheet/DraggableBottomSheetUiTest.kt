package com.github.leanite.countries.core.bottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DraggableBottomSheetUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersSheetContent() {
        setSingleSheet(
            handleContent = { DefaultHandle(HANDLE_TAG) },
            content = {
                Text(
                    text = "Sheet Content",
                    modifier = Modifier.testTag(SHEET_CONTENT_TAG)
                )
            }
        )

        composeRule.onNodeWithTag(SHEET_CONTENT_TAG).assertExists()
    }

    @Test
    fun rendersCustomHandleContent() {
        setSingleSheet(
            handleContent = {
                Text(
                    text = "Custom Handle",
                    modifier = Modifier.testTag(CUSTOM_HANDLE_TAG)
                )
            }
        )

        composeRule.onNodeWithTag(CUSTOM_HANDLE_TAG).assertExists()
    }

    @Test
    fun startsAtProvidedInitialState() {
        val host = setSingleSheet(
            initialState = BottomSheetState.Collapsed,
            handleContent = { DefaultHandle(HANDLE_TAG) }
        )

        composeRule.runOnIdle {
            assertEquals(BottomSheetState.Collapsed, host.state.currentState)
        }
    }

    @Test
    fun callsOnSheetStateChangedForInitialState() {
        val observedStates = mutableListOf<BottomSheetState>()

        setSingleSheet(
            initialState = BottomSheetState.Expanded,
            onSheetStateChanged = { observedStates.add(it) },
            handleContent = { DefaultHandle(HANDLE_TAG) }
        )

        composeRule.runOnIdle {
            assertTrue(observedStates.isNotEmpty())
            assertEquals(BottomSheetState.Expanded, observedStates.first())
        }
    }

    @Test
    fun callsOnSheetStateChangedWhenStateChanges() {
        val observedStates = mutableListOf<BottomSheetState>()

        setSingleSheet(
            initialState = BottomSheetState.Collapsed,
            onSheetStateChanged = { observedStates.add(it) },
            handleContent = { DefaultHandle(HANDLE_TAG) }
        )

        dragHandleUp(HANDLE_TAG)

        composeRule.waitUntil(5_000) {
            observedStates.any { it != BottomSheetState.Collapsed }
        }
        composeRule.runOnIdle {
            assertTrue(observedStates.any { it != BottomSheetState.Collapsed })
        }
    }

    @Test
    fun draggingHandleMovesSheet() {
        val host = setSingleSheet(
            initialState = BottomSheetState.Collapsed,
            handleContent = { DefaultHandle(HANDLE_TAG) }
        )

        var initialHeight = 0f
        composeRule.runOnIdle {
            initialHeight = host.state.sheetHeight.value
        }

        dragHandleUp(HANDLE_TAG)

        composeRule.waitUntil(5_000) { host.state.currentState != BottomSheetState.Collapsed }
        composeRule.runOnIdle {
            assertTrue(host.state.currentState != BottomSheetState.Collapsed)
            assertTrue(host.state.sheetHeight.value > initialHeight)
        }
    }

    @Test
    fun lazyColumnContentRemainsScrollable() {
        val host = setSingleSheet(
            initialState = BottomSheetState.Expanded,
            handleContent = { DefaultHandle(HANDLE_TAG) },
            content = { contentState ->
                LazyColumn(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .testTag(LIST_TAG),
                    state = contentState.listState
                ) {
                    items((0..200).toList()) { index ->
                        Text(text = "Item $index")
                    }
                }
            }
        )

        composeRule.onNodeWithTag(LIST_TAG).performTouchInput {
            swipeUp()
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            val hasScrolled = host.state.listState.firstVisibleItemIndex > 0 ||
                    host.state.listState.firstVisibleItemScrollOffset > 0
            assertTrue(hasScrolled)
        }
    }

    @Test
    fun downwardFlingWithListAwayFromTop_KeepsSheetExpanded_AndLetsListConsume() {
        val host = setSingleSheet(
            initialState = BottomSheetState.Expanded,
            handleContent = { DefaultHandle(HANDLE_TAG) },
            content = { contentState ->
                LazyColumn(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .testTag(LIST_TAG),
                    state = contentState.listState
                ) {
                    items((0..300).toList()) { index ->
                        Text(text = "Item $index")
                    }
                }
            }
        )

        repeat(5) {
            composeRule.onNodeWithTag(LIST_TAG).performTouchInput { swipeUp() }
        }
        composeRule.waitForIdle()

        var beforeIndex = 0
        var beforeOffset = 0
        composeRule.runOnIdle {
            beforeIndex = host.state.listState.firstVisibleItemIndex
            beforeOffset = host.state.listState.firstVisibleItemScrollOffset
            assertTrue(beforeIndex > 0 || beforeOffset > 0)
        }

        composeRule.onNodeWithTag(LIST_TAG).performTouchInput {
            swipeDown()
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            val afterIndex = host.state.listState.firstVisibleItemIndex
            val afterOffset = host.state.listState.firstVisibleItemScrollOffset

            assertEquals(BottomSheetState.Expanded, host.state.currentState)

            val movedTowardTop = afterIndex < beforeIndex ||
                    (afterIndex == beforeIndex && afterOffset < beforeOffset)
            assertTrue(movedTowardTop)
        }
    }

    @Test
    fun listScrollDoesNotMoveSheetInHandleOnlyMode() {
        val host = setSingleSheet(
            initialState = BottomSheetState.Half,
            scrollMode = BottomSheetScrollMode.HandleOnly,
            handleContent = { DefaultHandle(HANDLE_TAG) },
            content = { contentState ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(LIST_TAG),
                    state = contentState.listState
                ) {
                    items((0..200).toList()) { index ->
                        Text(text = "Item $index")
                    }
                }
            }
        )

        composeRule.onNodeWithTag(LIST_TAG).performTouchInput { swipeUp() }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            val hasScrolled = host.state.listState.firstVisibleItemIndex > 0 ||
                    host.state.listState.firstVisibleItemScrollOffset > 0
            assertTrue(hasScrolled)
            assertEquals(BottomSheetState.Half, host.state.currentState)
        }
    }

    @Test
    fun listDragMovesSheetInListAndHandleMode() {
        val host = setSingleSheet(
            initialState = BottomSheetState.Half,
            scrollMode = BottomSheetScrollMode.ListAndHandle,
            handleContent = { DefaultHandle(HANDLE_TAG) },
            content = { contentState ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(LIST_TAG),
                    state = contentState.listState
                ) {
                    items((0..200).toList()) { index ->
                        Text(text = "Item $index")
                    }
                }
            }
        )

        composeRule.onNodeWithTag(LIST_TAG).performTouchInput { swipeDown() }

        composeRule.waitUntil(5_000) {
            host.state.currentState == BottomSheetState.Collapsed
        }

        composeRule.runOnIdle {
            assertEquals(BottomSheetState.Collapsed, host.state.currentState)
        }
    }

    @Test
    fun twoSheetsDoNotInterfereWithEachOther() {
        val firstHost = SheetHost()
        val secondHost = SheetHost()

        composeRule.setContent {
            firstHost.state = rememberDraggableBottomSheetState(
                initialState = BottomSheetState.Half
            )
            secondHost.state = rememberDraggableBottomSheetState(
                initialState = BottomSheetState.Half
            )

            Column(modifier = Modifier.Companion.fillMaxSize()) {
                DraggableBottomSheet(
                    state = firstHost.state,
                    handleContent = { DefaultHandle(FIRST_HANDLE_TAG) }
                ) {
                    Text(text = "First Sheet")
                }

                DraggableBottomSheet(
                    state = secondHost.state,
                    handleContent = { DefaultHandle(SECOND_HANDLE_TAG) }
                ) {
                    Text(text = "Second Sheet")
                }
            }
        }
        composeRule.waitForIdle()

        dragHandleUp(FIRST_HANDLE_TAG)
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(BottomSheetState.Expanded, firstHost.state.currentState)
            assertEquals(BottomSheetState.Half, secondHost.state.currentState)
        }
    }

    @Test
    fun animationInterruptionDoesNotCrash_AndLeavesValidState() {
        val host = setSingleSheet(
            initialState = BottomSheetState.Half,
            handleContent = { DefaultHandle(HANDLE_TAG) }
        )

        dragHandleUp(HANDLE_TAG)
        dragHandleDown(HANDLE_TAG)
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            val isValidState = host.state.currentState == BottomSheetState.Collapsed ||
                    host.state.currentState == BottomSheetState.Half ||
                    host.state.currentState == BottomSheetState.Expanded

            assertTrue(isValidState)
            assertFalse(host.state.sheetHeight.value.isNaN())
        }
    }

    private fun setSingleSheet(
        initialState: BottomSheetState = BottomSheetState.Half,
        scrollMode: BottomSheetScrollMode = BottomSheetScrollMode.ListAndHandle,
        onSheetStateChanged: (BottomSheetState) -> Unit = {},
        handleContent: @Composable () -> Unit = { DefaultHandle(HANDLE_TAG) },
        content: @Composable ColumnScope.(DraggableBottomSheetContentState) -> Unit = {}
    ): SheetHost {
        val host = SheetHost()

        composeRule.setContent {
            host.state = rememberDraggableBottomSheetState(
                initialState = initialState,
                onSheetStateChanged = onSheetStateChanged,
                scrollMode = scrollMode
            )

            DraggableBottomSheet(
                state = host.state,
                handleContent = handleContent,
                content = content
            )
        }
        composeRule.waitForIdle()

        return host
    }

    private fun dragHandleUp(tag: String) {
        composeRule.onNodeWithTag(tag).performTouchInput {
            down(center)
            moveBy(Offset(0f, -900f))
            up()
        }
    }

    private fun dragHandleDown(tag: String) {
        composeRule.onNodeWithTag(tag).performTouchInput {
            down(center)
            moveBy(Offset(0f, 900f))
            up()
        }
    }

    @Composable
    private fun DefaultHandle(tag: String) {
        Box(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(40.dp)
                .testTag(tag)
        )
    }

    private class SheetHost {
        lateinit var state: DraggableBottomSheetState
    }

    private companion object {
        const val HANDLE_TAG = "sheet_handle"
        const val FIRST_HANDLE_TAG = "first_sheet_handle"
        const val SECOND_HANDLE_TAG = "second_sheet_handle"
        const val CUSTOM_HANDLE_TAG = "custom_handle"
        const val SHEET_CONTENT_TAG = "sheet_content"
        const val LIST_TAG = "sheet_list"
    }
}