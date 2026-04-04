package com.github.leanite.countries.core.bottomsheet

import com.github.leanite.countries.core.bottomsheet.fixture.defaultAnchors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class BottomSheetTargetStateResolverTest {

    private val anchors = defaultAnchors

    @Test
    fun `upward fling from Collapsed goes to Half`() {
        val target = resolveTargetState(
            velocityY = -1200f,
            fromState = BottomSheetState.Collapsed,
            currentScreenOffsetPx = anchors.collapsedScreenPx,
            anchors = anchors
        )

        assertEquals(BottomSheetState.Half, target)
    }

    @Test
    fun `upward fling from Half goes to Expanded`() {
        val target = resolveTargetState(
            velocityY = -1200f,
            fromState = BottomSheetState.Half,
            currentScreenOffsetPx = anchors.halfScreenPx,
            anchors = anchors
        )

        assertEquals(BottomSheetState.Expanded, target)
    }

    @Test
    fun `downward fling from Expanded goes to Half`() {
        val target = resolveTargetState(
            velocityY = 1200f,
            fromState = BottomSheetState.Expanded,
            currentScreenOffsetPx = anchors.expandedScreenPx,
            anchors = anchors
        )

        assertEquals(BottomSheetState.Half, target)
    }

    @Test
    fun `downward fling from Half goes to Collapsed`() {
        val target = resolveTargetState(
            velocityY = 1200f,
            fromState = BottomSheetState.Half,
            currentScreenOffsetPx = anchors.halfScreenPx,
            anchors = anchors
        )

        assertEquals(BottomSheetState.Collapsed, target)
    }

    @Test
    fun `very fast flings do not skip Half`() {
        val fromCollapsed = resolveTargetState(
            velocityY = -100_000f,
            fromState = BottomSheetState.Collapsed,
            currentScreenOffsetPx = anchors.collapsedScreenPx,
            anchors = anchors
        )

        val fromExpanded = resolveTargetState(
            velocityY = 100_000f,
            fromState = BottomSheetState.Expanded,
            currentScreenOffsetPx = anchors.expandedScreenPx,
            anchors = anchors
        )

        assertEquals(BottomSheetState.Half, fromCollapsed)
        assertEquals(BottomSheetState.Half, fromExpanded)
    }

    @Test
    fun `zero velocity selects Expanded when nearest`() {
        val target = resolveTargetState(
            velocityY = 0f,
            fromState = BottomSheetState.Half,
            currentScreenOffsetPx = 130f,
            anchors = anchors
        )

        assertEquals(BottomSheetState.Expanded, target)
    }

    @Test
    fun `zero velocity selects Half when nearest`() {
        val target = resolveTargetState(
            velocityY = 0f,
            fromState = BottomSheetState.Collapsed,
            currentScreenOffsetPx = 430f,
            anchors = anchors
        )

        assertEquals(BottomSheetState.Half, target)
    }

    @Test
    fun `zero velocity selects Collapsed when nearest`() {
        val target = resolveTargetState(
            velocityY = 0f,
            fromState = BottomSheetState.Half,
            currentScreenOffsetPx = 690f,
            anchors = anchors
        )

        assertEquals(BottomSheetState.Collapsed, target)
    }

    @Test
    fun `fractions outside 0f to 1f throw clear error`() {
        val exception = captureIllegalArgument {
            validateFractions(
                collapsedScreenFraction = 1.10f,
                halfScreenFraction = 0.40f,
                expandedScreenFraction = 0.05f
            )
        }

        assertTrue(
            "Expected clear range error message",
            exception.message.orEmpty().contains("between 0f and 1f")
        )
    }

    @Test
    fun `fractions with invalid order throw clear error`() {
        val exception = captureIllegalArgument {
            validateFractions(
                collapsedScreenFraction = 0.35f,
                halfScreenFraction = 0.45f,
                expandedScreenFraction = 0.10f
            )
        }

        assertTrue(
            "Expected clear ordering error message",
            exception.message.orEmpty().contains("expanded < half < collapsed")
        )
    }

    private fun captureIllegalArgument(block: () -> Unit): IllegalArgumentException {
        return try {
            block()
            fail("Expected IllegalArgumentException")
            throw AssertionError("Unreachable")
        } catch (exception: IllegalArgumentException) {
            exception
        }
    }
}
