package com.github.leanite.countries.core.data.fixture

import com.github.leanite.countries.core.data.model.local.CountryEntity

internal object CountryEntityFixtures {
    val oldCountryEntity = CountryEntity(
        code = "OLD",
        name = "Old",
        flagUrl = null,
        latitude = null,
        longitude = null,
        region = null,
        area = 0.0
    )

    val localBrazilEntity = CountryEntity(
        code = "BRA",
        name = "Brasil",
        flagUrl = "https://flagcdn.com/br.svg",
        latitude = -10.0,
        longitude = -55.0,
        region = "South America",
        area = 8515767.0
    )

    val argentinaEntity = CountryEntity(
        code = "ARG",
        name= "Argentina",
        flagUrl = "https://flagcdn.com/ar.svg",
        latitude = -34.0,
        longitude = -64.0,
        region = "South America",
        area = 2780400.0,
    )

    val entityList = listOf(localBrazilEntity, argentinaEntity)

    val fakeBordersCountries = listOf(
        CountryEntity("ARG", "Argentina", "arg.svg", -34.0, -64.0, "South America", 2780400.0),
        CountryEntity("URY", "Uruguai", "ury.svg", -32.0, -56.0, "South America", 181034.0)
    )
}
