package com.github.leanite.countries.core.data.datasource

import com.github.leanite.countries.core.data.network.CountriesApiService
import com.github.leanite.countries.core.data.model.network.CountryDTO
import com.github.leanite.countries.core.data.model.network.CountryDetailsDTO
import javax.inject.Inject

internal interface CountryRemoteDataSource {
    suspend fun getAllCountries(): List<CountryDTO>
    suspend fun getCountryDetails(code: String): CountryDetailsDTO
    suspend fun getCountry(code: String): CountryDTO
}

internal class CountryRemoteDataSourceImpl @Inject constructor(
    private val apiService: CountriesApiService
) : CountryRemoteDataSource {
    override suspend fun getAllCountries(): List<CountryDTO> = apiService.getAllCountries()

    override suspend fun getCountryDetails(code: String): CountryDetailsDTO {
        return apiService.getCountryDetails(code)
    }

    override suspend fun getCountry(code: String): CountryDTO {
        return apiService.getCountry(code)
    }
}