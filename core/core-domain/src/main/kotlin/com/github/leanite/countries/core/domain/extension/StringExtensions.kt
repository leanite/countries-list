package com.github.leanite.countries.core.domain.extension

import java.text.Normalizer
import kotlin.text.iterator

fun String.normalizedSortKey(): String {
    val normalized = Normalizer.normalize(trim(), Normalizer.Form.NFD)
    return buildString(normalized.length) {
        for (char in normalized) {
            if (Character.getType(char) != Character.NON_SPACING_MARK.toInt()) {
                append(char.lowercaseChar())
            }
        }
    }
}
