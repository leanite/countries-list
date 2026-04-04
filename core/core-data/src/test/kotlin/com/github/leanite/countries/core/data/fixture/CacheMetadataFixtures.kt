package com.github.leanite.countries.core.data.fixture

import com.github.leanite.countries.core.data.model.local.CacheMetadataEntity

internal val allCountriesCacheMetadata = CacheMetadataEntity(
    key = "countries_all",
    updatedAtMillis = 987_654L
)

internal val countryCacheBraMetadata = CacheMetadataEntity(
    key = "country_BRA",
    updatedAtMillis = 111L
)

internal val countryDetailsCacheBraMetadata = CacheMetadataEntity(
    key = "country_details_BRA",
    updatedAtMillis = 333L
)
