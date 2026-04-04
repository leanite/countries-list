package com.github.leanite.countries.core.data.mapper

import com.github.leanite.countries.core.data.model.local.CountryEntity
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryCode
import com.github.leanite.countries.core.domain.model.Location

internal fun CountryEntity.toDomain(): Country {
    val location = if (latitude != null && longitude != null) {
        Location(latitude, longitude)
    } else {
        null
    }
    return Country(
        code = CountryCode(code),
        name = name,
        flagUrl = flagUrl,
        location = location,
        region = region,
        area = area
    )
}

internal fun Country.toEntity(): CountryEntity? {
    val codeValue = code?.value?.trim().orEmpty()
    val nameValue = name?.trim().orEmpty()
    if (codeValue.isBlank() || nameValue.isBlank()) return null

    return CountryEntity(
        code = codeValue,
        name = nameValue,
        flagUrl = flagUrl,
        latitude = location?.latitude,
        longitude = location?.longitude,
        region = region,
        area = area
    )
}
