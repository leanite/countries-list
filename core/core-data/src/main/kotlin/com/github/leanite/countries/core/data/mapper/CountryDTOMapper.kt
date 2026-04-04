package com.github.leanite.countries.core.data.mapper

import com.github.leanite.countries.core.data.model.network.CountryDTO
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryCode
import com.github.leanite.countries.core.domain.model.Location

internal fun CountryDTO.toDomain(): Country {
    val lat = latlng?.getOrNull(0)
    val lng = latlng?.getOrNull(1)
    val location = if (lat != null && lng != null) Location(lat, lng) else null
    val code = cca3?.let { CountryCode(it) }

    return Country(
        code = code,
        name = this.translations?.portuguese?.common,
        flagUrl = flags?.svg,
        location = location,
        region = region,
        area = area
    )
}
