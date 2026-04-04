package com.github.leanite.countries.feature.list.extension

import java.text.Normalizer
import kotlin.text.iterator

internal fun String.normalizedSortKey(): String {
    val normalized = Normalizer.normalize(trim(), Normalizer.Form.NFD)
    return buildString(normalized.length) {
        for (char in normalized) {
            if (Character.getType(char) != Character.NON_SPACING_MARK.toInt()) {
                append(char.lowercaseChar())
            }
        }
    }
}

internal fun String.toSectionLetter(): Char {
    val normalized = Normalizer.normalize(trim(), Normalizer.Form.NFD)

    val firstBaseChar = normalized.firstOrNull { ch ->
        Character.getType(ch) != Character.NON_SPACING_MARK.toInt()
    }
    val letter = firstBaseChar?.uppercaseChar() ?: '#'

    return if (letter in 'A'..'Z') letter else '#'
}
