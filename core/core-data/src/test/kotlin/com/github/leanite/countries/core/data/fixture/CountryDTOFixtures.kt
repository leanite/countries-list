package com.github.leanite.countries.core.data.fixture

import com.github.leanite.countries.core.data.model.network.CountryDTO
import com.github.leanite.countries.core.data.model.network.FlagsDTO
import com.github.leanite.countries.core.data.model.network.PortugueseLanguageDTO
import com.github.leanite.countries.core.data.model.network.TranslationsDTO
import kotlin.collections.listOf

internal object CountryDTOFixtures {
    val remoteBrazilDto = CountryDTO(
        cca3 = "BRA",
        translations = TranslationsDTO(PortugueseLanguageDTO(common = "Brasil")),
        flags = FlagsDTO(
            png = "https://flagcdn.com/w320/br.png",
            svg = "https://flagcdn.com/br.svg",
            alt = "Flag of Brazil"
        ),
        latlng = listOf(-10.0, -55.0),
        region = "South America",
        area = 8515767.0
    )

    val remoteNullDto = CountryDTO(
        cca3 = null,
        translations = null,
        flags = null,
        latlng = null,
        region = null,
        area = null
    )

    val emptyLatLngDto = CountryDTO(
        cca3 = "NOC",
        translations = TranslationsDTO(PortugueseLanguageDTO(common = "No Coordinates")),
        flags = FlagsDTO(svg = "https://flagcdn.com/test.svg"),
        latlng = emptyList(),
        region = "Europe",
        area = 3.0
    )

    val faultyLatLngDto = CountryDTO(
        cca3 = "PRT",
        translations = TranslationsDTO(PortugueseLanguageDTO(common = "Partial Coordinates")),
        flags = FlagsDTO(svg = "https://flagcdn.com/partial.svg"),
        latlng = listOf(-10.0),
        region = "Europe",
        area = 3.0
    )

    val remoteListWithNull = listOf(
        CountryDTO(
            cca3 = "BRA",
            translations = TranslationsDTO(PortugueseLanguageDTO(common = "Brasil")),
            flags = FlagsDTO(svg = "https://flagcdn.com/br.svg"),
            latlng = listOf(-10.0, -55.0),
            region = "South America",
            area = 8515767.0
        ),
        CountryDTO(
            cca3 = "UNK",
            translations = null,
            flags = FlagsDTO(svg = "https://flagcdn.com/unknown.svg"),
            latlng = listOf(0.0, 0.0),
            region = null,
            area = 0.0
        )
    )
}
