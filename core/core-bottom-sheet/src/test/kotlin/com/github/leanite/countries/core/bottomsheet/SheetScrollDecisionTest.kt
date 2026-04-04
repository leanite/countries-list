package com.github.leanite.countries.core.bottomsheet

import com.github.leanite.countries.core.bottomsheet.fixture.defaultAnchors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SheetScrollDecisionTest {

    private val decision = SheetScrollDecision(defaultAnchors)

    @Test
    fun `should consume pre scroll when scrolling up and sheet can expand`() {
        val result = decision.shouldConsumePreScroll(
            deltaY = -10f,
            currentOffsetPx = 400f // above expanded (100f), so can expand
        )

        assertTrue(result)
    }

    @Test
    fun `should not consume pre scroll when scrolling down`() {
        val result = decision.shouldConsumePreScroll(
            deltaY = 10f,
            currentOffsetPx = 400f
        )

        assertFalse(result)
    }

    @Test
    fun `should not consume pre scroll when sheet is fully expanded`() {
        val result = decision.shouldConsumePreScroll(
            deltaY = -10f,
            currentOffsetPx = 100f // at expanded position
        )

        assertFalse(result)
    }

    @Test
    fun `should consume post scroll when scrolling down and list at top`() {
        val result = decision.shouldConsumePostScroll(
            deltaY = 10f,
            isListAtTop = true
        )

        assertTrue(result)
    }

    @Test
    fun `should not consume post scroll when scrolling up`() {
        val result = decision.shouldConsumePostScroll(
            deltaY = -10f,
            isListAtTop = true
        )

        assertFalse(result)
    }

    @Test
    fun `should not consume post scroll when list is not at top`() {
        val result = decision.shouldConsumePostScroll(
            deltaY = 10f,
            isListAtTop = false
        )

        assertFalse(result)
    }

    @Test
    fun `should return null when flinging down and list is not at top`() {
        val result = decision.resolvePreFlingTarget(
            velocityY = 1200f,
            isListAtTop = false,
            gestureStartState = null,
            currentState = BottomSheetState.Expanded,
            currentOffsetPx = defaultAnchors.expandedScreenPx
        )

        assertNull(result)
    }

    @Test
    fun `should return target when flinging up from collapsed`() {
        val result = decision.resolvePreFlingTarget(
            velocityY = -1200f,
            isListAtTop = true,
            gestureStartState = BottomSheetState.Collapsed,
            currentState = BottomSheetState.Collapsed,
            currentOffsetPx = defaultAnchors.collapsedScreenPx
        )

        assertEquals(BottomSheetState.Half, result)
    }

    @Test
    fun `should return null when target equals current state`() {
        val result = decision.resolvePreFlingTarget(
            velocityY = -1200f,
            isListAtTop = true,
            gestureStartState = BottomSheetState.Half,
            currentState = BottomSheetState.Expanded,
            currentOffsetPx = defaultAnchors.halfScreenPx
        )

        assertNull(result)
    }

    @Test
    fun `should use current state when gesture start state is null`() {
        val result = decision.resolvePreFlingTarget(
            velocityY = -1200f,
            isListAtTop = true,
            gestureStartState = null,
            currentState = BottomSheetState.Collapsed,
            currentOffsetPx = defaultAnchors.collapsedScreenPx
        )

        assertEquals(BottomSheetState.Half, result)
    }

    @Test
    fun `should return target when flinging down with list at top`() {
        val result = decision.resolvePreFlingTarget(
            velocityY = 1200f,
            isListAtTop = true,
            gestureStartState = BottomSheetState.Expanded,
            currentState = BottomSheetState.Expanded,
            currentOffsetPx = defaultAnchors.expandedScreenPx
        )

        assertEquals(BottomSheetState.Half, result)
    }

}
