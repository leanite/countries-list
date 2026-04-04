package com.github.leanite.countries.feature.details.extension

fun String?.orDash(): String = if (this.isNullOrBlank()) "-" else this

fun List<String>?.joinOrDash(): String =
    if (this.isNullOrEmpty()) "-" else this.joinToString(", ")