package com.github.leanite.countries.core.bottomsheet

import kotlin.math.abs

internal data class BottomSheetAnchorOffsets(
    val expandedScreenPx: Float,
    val halfScreenPx: Float,
    val collapsedScreenPx: Float
)

internal fun resolveTargetState(
    velocityY: Float,
    fromState: BottomSheetState,
    currentScreenOffsetPx: Float,
    anchors: BottomSheetAnchorOffsets
): BottomSheetState {
    return when {
        velocityY < 0f -> fromState.nextExpandedState()
        velocityY > 0f -> fromState.nextCollapsedState()
        else -> closestState(currentScreenOffsetPx, anchors)
    }
}

internal fun BottomSheetState.nextExpandedState(): BottomSheetState = when (this) {
    BottomSheetState.Collapsed -> BottomSheetState.Half
    BottomSheetState.Half -> BottomSheetState.Expanded
    BottomSheetState.Expanded -> BottomSheetState.Expanded
}

internal fun BottomSheetState.nextCollapsedState(): BottomSheetState = when (this) {
    BottomSheetState.Expanded -> BottomSheetState.Half
    BottomSheetState.Half -> BottomSheetState.Collapsed
    BottomSheetState.Collapsed -> BottomSheetState.Collapsed
}

internal fun closestState(
    currentScreenOffsetPx: Float,
    anchors: BottomSheetAnchorOffsets
): BottomSheetState {
    val expandedDistance = abs(currentScreenOffsetPx - anchors.expandedScreenPx)
    val halfDistance = abs(currentScreenOffsetPx - anchors.halfScreenPx)
    val collapsedDistance = abs(currentScreenOffsetPx - anchors.collapsedScreenPx)

    return when {
        expandedDistance <= halfDistance && expandedDistance <= collapsedDistance -> BottomSheetState.Expanded
        halfDistance <= collapsedDistance -> BottomSheetState.Half
        else -> BottomSheetState.Collapsed
    }
}
