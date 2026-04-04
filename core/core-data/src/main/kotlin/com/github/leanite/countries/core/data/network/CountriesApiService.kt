package com.github.leanite.countries.core.data.network

import com.github.leanite.countries.core.data.model.network.CountryDTO
import com.github.leanite.countries.core.data.model.network.CountryDetailsDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface CountriesApiService {
    @GET("v3.1/all")
    suspend fun getAllCountries(
        @Query("fields") fields: String = "cca3,translations,flags,latlng,area,region"
    ): List<CountryDTO>

    @GET("v3.1/alpha/{code}")
    suspend fun getCountryDetails(
        @Path("code") name: String,
        @Query("fields") fields: String =
            "cca3,translations,capital,region,population,area,timezones,currencies,borders,flags"
    ): CountryDetailsDTO

    @GET("v3.1/alpha/{code}")
    suspend fun getCountry(
        @Path("code") code: String,
        @Query("fields") fields: String = "cca3,translations,flags,latlng,area,region"
    ): CountryDTO
}
