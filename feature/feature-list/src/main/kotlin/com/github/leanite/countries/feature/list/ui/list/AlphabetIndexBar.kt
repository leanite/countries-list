package com.github.leanite.countries.feature.list.ui.list

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlphabetIndexBar(
    indexByLetter: Map<Char, Int>,
    onLetterSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val resolver = remember { AlphabetIndexResolver() }
    var containerHeightPx by remember { mutableFloatStateOf(1f) }
    var lastSelectedLetter by remember { mutableStateOf<Char?>(null) }

    fun handleLetterSelection(y: Float) {
        val selectedLetter = resolver.resolveLetterFromY(
            y = y,
            containerHeightPx = containerHeightPx,
            indexByLetter = indexByLetter
        ) ?: return

        if (selectedLetter == lastSelectedLetter) return
        lastSelectedLetter = selectedLetter
        onLetterSelected(indexByLetter.getValue(selectedLetter))
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(20.dp)
            .onSizeChanged { size ->
                containerHeightPx = size.height.toFloat().coerceAtLeast(1f)
            }
            .pointerInput(indexByLetter) {
                detectTapGestures(
                    onPress = { offset ->
                        handleLetterSelection(offset.y)
                        tryAwaitRelease()
                        lastSelectedLetter = null
                    }
                )
            }
            .pointerInput(indexByLetter) {
                detectDragGestures(
                    onDragStart = { offset ->
                        handleLetterSelection(offset.y)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        handleLetterSelection(change.position.y)
                    },
                    onDragEnd = {
                        lastSelectedLetter = null
                    },
                    onDragCancel = {
                        lastSelectedLetter = null
                    }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        resolver.letters.forEach { letter ->
            val enabled = indexByLetter.containsKey(letter)
            Text(
                text = letter.toString(),
                modifier = Modifier.semantics {
                    if (!enabled) disabled()
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    lineHeight = 9.sp
                ),
                textAlign = TextAlign.Center,
                color = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.30f)
                }
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun AlphabetIndexBarPreview() {
    AlphabetIndexBar(
        indexByLetter = mapOf('A' to 0, 'B' to 3, 'C' to 7, 'M' to 15, 'Z' to 30),
        onLetterSelected = {}
    )
}

internal class AlphabetIndexResolver(
    val letters: List<Char> = ('A'..'Z').toList() + '#'
) {
    fun resolveLetterFromY(
        y: Float,
        containerHeightPx: Float,
        indexByLetter: Map<Char, Int>
    ): Char? {
        val clampedY = y.coerceIn(0f, containerHeightPx - 1f)
        val rawIndex = ((clampedY / containerHeightPx) * letters.size)
            .toInt()
            .coerceIn(0, letters.lastIndex)

        return findNearestAvailableLetter(
            startIndex = rawIndex,
            indexByLetter = indexByLetter
        )
    }

    fun findNearestAvailableLetter(
        startIndex: Int,
        indexByLetter: Map<Char, Int>
    ): Char? {
        for (distance in 0..letters.lastIndex) {
            val lower = startIndex - distance
            if (lower >= 0) {
                val letter = letters[lower]
                if (indexByLetter.containsKey(letter)) return letter
            }

            val upper = startIndex + distance
            if (upper <= letters.lastIndex) {
                val letter = letters[upper]
                if (indexByLetter.containsKey(letter)) return letter
            }
        }
        return null
    }
}