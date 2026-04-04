package com.github.leanite.countries.core.domain.repository

import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryDetails
import com.github.leanite.countries.core.domain.result.AppResult

interface CountryRepository {
    suspend fun getCountries(): AppResult<List<Country>>
    suspend fun getCountryDetails(code: String): AppResult<CountryDetails>
    suspend fun getCountry(code: String): AppResult<Country>
}