package com.github.leanite.countries.feature.list.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.leanite.countries.feature.list.ui.model.RegionFilter
import com.github.leanite.countries.feature.list.ui.model.defaultRegionFilters
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun CountryListHandle(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    regionFilters: ImmutableList<RegionFilter>,
    selectedRegionFilter: String,
    onRegionFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            singleLine = true,
            label = { Text("Buscar país") },
            placeholder = { Text("Digite o nome") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        ContinentFiltersCarousel(
            filters = regionFilters,
            selectedFilter = selectedRegionFilter,
            onFilterSelected = onRegionFilterSelected,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ContinentFiltersCarousel(
    filters: ImmutableList<RegionFilter>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(items = filters, key = { it.key }) { filter ->
            FilterChip(
                selected = filter.key == selectedFilter,
                onClick = { onFilterSelected(filter.key) },
                label = { Text(filter.display) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CountryListHandlePreview() {
    CountryListHandle(
        searchQuery = "",
        onSearchQueryChanged = {},
        regionFilters = defaultRegionFilters.toImmutableList(),
        selectedRegionFilter = "ALL",
        onRegionFilterSelected = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun CountryListHandleWithQueryPreview() {
    CountryListHandle(
        searchQuery = "Bra",
        onSearchQueryChanged = {},
        regionFilters = defaultRegionFilters.toImmutableList(),
        selectedRegionFilter = "Americas",
        onRegionFilterSelected = {}
    )
}