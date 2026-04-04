package com.github.leanite.countries.core.bottomsheet

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection

internal interface BottomSheetScrollStrategy {
    fun createConnection(
        bottomSheetState: AnchoredDraggableState<BottomSheetState>,
        listState: LazyListState,
        anchors: BottomSheetAnchorOffsets,
        onFling: (velocity: Float, targetState: BottomSheetState) -> Unit
    ): NestedScrollConnection?
}

internal object HandleOnlyScrollStrategy : BottomSheetScrollStrategy {
    override fun createConnection(
        bottomSheetState: AnchoredDraggableState<BottomSheetState>,
        listState: LazyListState,
        anchors: BottomSheetAnchorOffsets,
        onFling: (velocity: Float, targetState: BottomSheetState) -> Unit
    ): NestedScrollConnection? = null
}

internal object ListAndHandleScrollStrategy : BottomSheetScrollStrategy {
    override fun createConnection(
        bottomSheetState: AnchoredDraggableState<BottomSheetState>,
        listState: LazyListState,
        anchors: BottomSheetAnchorOffsets,
        onFling: (velocity: Float, targetState: BottomSheetState) -> Unit
    ): NestedScrollConnection = SheetNestedScrollConnection(
        bottomSheetState = bottomSheetState,
        listState = listState,
        decision = SheetScrollDecision(anchors),
        onFling = onFling
    )
}

internal fun BottomSheetScrollMode.toStrategy(): BottomSheetScrollStrategy = when (this) {
    BottomSheetScrollMode.HandleOnly -> HandleOnlyScrollStrategy
    BottomSheetScrollMode.ListAndHandle -> ListAndHandleScrollStrategy
}
