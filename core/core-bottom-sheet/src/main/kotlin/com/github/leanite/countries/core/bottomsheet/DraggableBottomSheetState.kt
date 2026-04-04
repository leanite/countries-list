package com.github.leanite.countries.core.bottomsheet

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

@ConsistentCopyVisibility
data class DraggableBottomSheetState internal constructor(
    internal val anchoredDraggableState: AnchoredDraggableState<BottomSheetState>,
    val listState: LazyListState,
    internal val nestedScrollConnection: NestedScrollConnection?,
    val sheetHeight: Dp
) {
    val currentState: BottomSheetState
        get() = anchoredDraggableState.currentValue
}

enum class BottomSheetState {
    Collapsed,
    Half,
    Expanded
}

internal object BottomSheetBehaviorDefaults {
    const val CollapsedScreenFraction = 0.60f
    const val HalfScreenFraction = 0.40f
    const val ExpandedScreenFraction = 0.05f
    const val AnimationDurationMs = 300
}

@Composable
fun rememberDraggableBottomSheetState(
    initialState: BottomSheetState = BottomSheetState.Half,
    onSheetStateChanged: (BottomSheetState) -> Unit = {},
    collapsedScreenFraction: Float = BottomSheetBehaviorDefaults.CollapsedScreenFraction,
    halfScreenFraction: Float = BottomSheetBehaviorDefaults.HalfScreenFraction,
    expandedScreenFraction: Float = BottomSheetBehaviorDefaults.ExpandedScreenFraction,
    scrollMode: BottomSheetScrollMode = BottomSheetScrollMode.ListAndHandle
): DraggableBottomSheetState {
    validateFractions(
        collapsedScreenFraction = collapsedScreenFraction,
        halfScreenFraction = halfScreenFraction,
        expandedScreenFraction = expandedScreenFraction
    )

    val density = LocalDensity.current
    val screenHeightPx = LocalWindowInfo.current.containerSize.height.toFloat()

    val expandedScreenOffsetPx = screenHeightPx * expandedScreenFraction
    val halfScreenOffsetPx = screenHeightPx * halfScreenFraction
    val collapsedScreenOffsetPx = screenHeightPx * collapsedScreenFraction

    val anchors = DraggableAnchors {
        BottomSheetState.Collapsed at collapsedScreenOffsetPx
        BottomSheetState.Half at halfScreenOffsetPx
        BottomSheetState.Expanded at expandedScreenOffsetPx
    }

    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = initialState,
            anchors = anchors
        )
    }

    LaunchedEffect(draggableState.currentValue) {
        onSheetStateChanged(draggableState.currentValue)
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val anchorOffsets = BottomSheetAnchorOffsets(
        expandedScreenPx = expandedScreenOffsetPx,
        halfScreenPx = halfScreenOffsetPx,
        collapsedScreenPx = collapsedScreenOffsetPx
    )

    val nestedScrollConnection = remember(draggableState, listState, anchorOffsets, scrollMode) {
        scrollMode.toStrategy().createConnection(
            bottomSheetState = draggableState,
            listState = listState,
            anchors = anchorOffsets,
            onFling = { _, targetState ->
                scope.launch {
                    draggableState.animateTo(
                        targetState,
                        tween(durationMillis = BottomSheetBehaviorDefaults.AnimationDurationMs)
                    )
                }
            }
        )
    }

    val sheetHeight by remember(draggableState, density, screenHeightPx, halfScreenOffsetPx) {
        derivedStateOf {
            val currentScreenOffset = draggableState.offset
            val safeScreenOffset = if (currentScreenOffset.isNaN()) halfScreenOffsetPx else currentScreenOffset
            with(density) { (screenHeightPx - safeScreenOffset).toDp() }
        }
    }

    return DraggableBottomSheetState(
        anchoredDraggableState = draggableState,
        listState = listState,
        nestedScrollConnection = nestedScrollConnection,
        sheetHeight = sheetHeight
    )
}

internal fun validateFractions(
    collapsedScreenFraction: Float,
    halfScreenFraction: Float,
    expandedScreenFraction: Float
) {
    require(collapsedScreenFraction in 0f..1f) { "collapsedScreenFraction must be between 0f and 1f" }
    require(halfScreenFraction in 0f..1f) { "halfScreenFraction must be between 0f and 1f" }
    require(expandedScreenFraction in 0f..1f) { "expandedScreenFraction must be between 0f and 1f" }
    require(expandedScreenFraction < halfScreenFraction && halfScreenFraction < collapsedScreenFraction) {
        "Invalid fractions: expected expanded < half < collapsed"
    }
}
