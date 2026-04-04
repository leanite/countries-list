package com.github.leanite.countries.feature.list.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class RegionFilter(
    val key: String,
    val display: String
)

internal const val ALL_REGION_FILTER_KEY = "ALL"

internal val defaultRegionFilters = listOf(
    RegionFilter(ALL_REGION_FILTER_KEY, "A-Z"),
    RegionFilter("Americas", "Américas"),
    RegionFilter("Africa", "África"),
    RegionFilter("Europe", "Europa"),
    RegionFilter("Asia", "Ásia"),
    RegionFilter("Oceania", "Oceania"),
    RegionFilter("Antarctic", "Antártida"),
)
