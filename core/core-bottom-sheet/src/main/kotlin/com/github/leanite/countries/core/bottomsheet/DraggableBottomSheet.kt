package com.github.leanite.countries.core.bottomsheet

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class DraggableBottomSheetColors(
    val containerColor: Color,
    val handleColor: Color
)

@Immutable
data class DraggableBottomSheetStyle(
    val shadowElevation: Dp,
    val cornerRadius: Dp,
    val handleWidth: Dp,
    val handleHeight: Dp,
    val handleVerticalPadding: Dp,
    val positionalThreshold: Float
)

object DraggableBottomSheetDefaults {
    fun colors(
        containerColor: Color = Color.White,
        handleColor: Color = Color(0xFFF3F3F3)
    ) = DraggableBottomSheetColors(
        containerColor = containerColor,
        handleColor = handleColor
    )

    fun style(
        shadowElevation: Dp = 16.dp,
        cornerRadius: Dp = 16.dp,
        handleWidth: Dp = 48.dp,
        handleHeight: Dp = 4.dp,
        handleVerticalPadding: Dp = 12.dp,
        positionalThreshold: Float = 0.5f
    ) = DraggableBottomSheetStyle(
        shadowElevation = shadowElevation,
        cornerRadius = cornerRadius,
        handleWidth = handleWidth,
        handleHeight = handleHeight,
        handleVerticalPadding = handleVerticalPadding,
        positionalThreshold = positionalThreshold
    )
}

data class DraggableBottomSheetContentState(
    val listState: LazyListState,
    val sheetState: BottomSheetState
)

@Composable
fun DraggableBottomSheet(
    state: DraggableBottomSheetState,
    modifier: Modifier = Modifier,
    colors: DraggableBottomSheetColors = DraggableBottomSheetDefaults.colors(),
    style: DraggableBottomSheetStyle = DraggableBottomSheetDefaults.style(),
    handleContent: @Composable () -> Unit = {},
    content: @Composable ColumnScope.(DraggableBottomSheetContentState) -> Unit
) {
    val nestedScrollModifier =
        if (state.nestedScrollConnection != null) {
            Modifier.nestedScroll(state.nestedScrollConnection)
        } else {
            Modifier
        }
    val sheetShape = RoundedCornerShape(
        topStart = style.cornerRadius,
        topEnd = style.cornerRadius
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(state.sheetHeight)
            .then(nestedScrollModifier)
            .shadow(style.shadowElevation, sheetShape)
            .clip(sheetShape)
            .background(colors.containerColor)
    ) {
        DragHandle(
            anchoredDraggableState = state.anchoredDraggableState,
            extraContent = handleContent,
            colors = colors,
            style = style
        )

        content(
            DraggableBottomSheetContentState(
                listState = state.listState,
                sheetState = state.currentState,
            )
        )
    }
}

@Preview(showBackground = true, heightDp = 600)
@Composable
private fun DraggableBottomSheetPreview() {
    DraggableBottomSheet(
        state = rememberDraggableBottomSheetState(initialState = BottomSheetState.Half)
    ) {
        Text(
            text = "Sheet content",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun DragHandle(
    anchoredDraggableState: AnchoredDraggableState<BottomSheetState>,
    modifier: Modifier = Modifier,
    colors: DraggableBottomSheetColors,
    style: DraggableBottomSheetStyle,
    extraContent: @Composable () -> Unit = {},
) {
    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = anchoredDraggableState,
        positionalThreshold = { distance -> distance * style.positionalThreshold },
        animationSpec = tween(durationMillis = BottomSheetBehaviorDefaults.AnimationDurationMs)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .anchoredDraggable(
                state = anchoredDraggableState,
                orientation = Orientation.Vertical,
                flingBehavior = flingBehavior
            )
            .padding(top = style.handleVerticalPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = style.handleWidth,
                    height = style.handleHeight
                )
                .clip(RoundedCornerShape(style.handleHeight / 2))
                .background(colors.handleColor)
        )
        extraContent()
    }
}
