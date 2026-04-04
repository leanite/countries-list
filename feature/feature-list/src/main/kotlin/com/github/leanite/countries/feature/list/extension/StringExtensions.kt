package com.github.leanite.countries.feature.list.extension

import java.text.Normalizer

internal fun String.toSectionLetter(): Char {
    val normalized = Normalizer.normalize(trim(), Normalizer.Form.NFD)

    val firstBaseChar = normalized.firstOrNull { ch ->
        Character.getType(ch) != Character.NON_SPACING_MARK.toInt()
    }
    val letter = firstBaseChar?.uppercaseChar() ?: '#'

    return if (letter in 'A'..'Z') letter else '#'
}
