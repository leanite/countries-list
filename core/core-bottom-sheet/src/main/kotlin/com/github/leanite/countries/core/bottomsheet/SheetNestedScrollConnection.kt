package com.github.leanite.countries.core.bottomsheet

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

internal class SheetNestedScrollConnection(
    private val bottomSheetState: AnchoredDraggableState<BottomSheetState>,
    private val listState: LazyListState,
    private val decision: SheetScrollDecision,
    private val onFling: (velocity: Float, targetState: BottomSheetState) -> Unit
) : NestedScrollConnection {

    private var gestureStartState: BottomSheetState? = null

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (source != NestedScrollSource.UserInput) return Offset.Zero

        captureGestureStartState()

        val delta = available.y

        return if (decision.shouldConsumePreScroll(delta, bottomSheetState.offset)) {
            val consumed = bottomSheetState.dispatchRawDelta(delta)
            Offset(0f, consumed)
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (source != NestedScrollSource.UserInput) return Offset.Zero

        val delta = available.y

        return if (decision.shouldConsumePostScroll(delta, isListAtTop())) {
            val consumedDelta = bottomSheetState.dispatchRawDelta(delta)
            Offset(0f, consumedDelta)
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val target = decision.resolvePreFlingTarget(
            velocityY = available.y,
            isListAtTop = isListAtTop(),
            gestureStartState = gestureStartState,
            currentState = bottomSheetState.currentValue,
            currentOffsetPx = bottomSheetState.offset
        )

        resetGestureState()

        if (target == null) return Velocity.Zero

        onFling(available.y, target)
        return Velocity(0f, available.y)
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        return Velocity.Zero
    }

    private fun captureGestureStartState() {
        if (gestureStartState == null) {
            gestureStartState = bottomSheetState.currentValue
        }
    }

    private fun resetGestureState() {
        gestureStartState = null
    }

    private fun isListAtTop(): Boolean {
        return listState.firstVisibleItemIndex == 0 &&
                listState.firstVisibleItemScrollOffset == 0
    }
}

internal class SheetScrollDecision(
    private val anchors: BottomSheetAnchorOffsets
) {
    fun shouldConsumePreScroll(
        deltaY: Float,
        currentOffsetPx: Float
    ): Boolean {
        val isScrollingUp = deltaY < 0
        val canExpand = currentOffsetPx > anchors.expandedScreenPx
        return isScrollingUp && canExpand
    }

    fun shouldConsumePostScroll(
        deltaY: Float,
        isListAtTop: Boolean
    ): Boolean {
        return deltaY > 0 && isListAtTop
    }

    fun resolvePreFlingTarget(
        velocityY: Float,
        isListAtTop: Boolean,
        gestureStartState: BottomSheetState?,
        currentState: BottomSheetState,
        currentOffsetPx: Float
    ): BottomSheetState? {
        if (velocityY > 0 && !isListAtTop) return null

        val fromState = gestureStartState ?: currentState
        val target = resolveTargetState(velocityY, fromState, currentOffsetPx, anchors)

        return if (target != currentState) target else null
    }
}
