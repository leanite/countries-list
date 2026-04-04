package com.github.leanite.countries.core.data.repository

import com.github.leanite.countries.core.data.datasource.CountryLocalDataSource
import com.github.leanite.countries.core.data.datasource.CountryRemoteDataSource
import com.github.leanite.countries.core.data.error.toAppError
import com.github.leanite.countries.core.data.error.toDataError
import com.github.leanite.countries.core.data.mapper.toDomain
import com.github.leanite.countries.core.data.mapper.toEntity
import com.github.leanite.countries.core.domain.result.AppError
import com.github.leanite.countries.core.domain.model.Border
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.model.CountryDetails
import com.github.leanite.countries.core.domain.repository.CountryRepository
import com.github.leanite.countries.core.domain.result.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

internal class CountryRepositoryImpl @Inject constructor(
    private val remoteDataSource: CountryRemoteDataSource,
    private val localDataSource: CountryLocalDataSource,
    @Named("cache_ttl_ms") private val cacheTtlMillis: Long,
    @Named("io") private val ioDispatcher: CoroutineDispatcher
) : CountryRepository {

    private val cacheValidator = CacheValidator(localDataSource, cacheTtlMillis)

    override suspend fun getCountries(): AppResult<List<Country>> = withContext(ioDispatcher) {
        val isCacheValid = cacheValidator.isAllCountriesValid()
        val localCountries = localDataSource.getAllCountries().map { it.toDomain() }

        if (localCountries.isNotEmpty() && isCacheValid) {
            return@withContext AppResult.Success(localCountries)
        }

        return@withContext try {
            val remoteCountries = getAllRemoteCountries()

            replaceAllLocalCountries(remoteCountries)

            AppResult.Success(remoteCountries)
        } catch (throwable: Throwable) {
            if (localCountries.isNotEmpty()) {
                AppResult.Success(localCountries) // fallback offline-first
            } else {
                AppResult.Error(throwable.toDataError().toAppError())
            }
        }
    }

    override suspend fun getCountry(code: String): AppResult<Country> = withContext(ioDispatcher) {
        val localCountry = localDataSource.getCountryByCode(code)?.toDomain()
        if (localCountry != null && cacheValidator.isCountryValid(localCountry.code?.value.orEmpty())) {
            return@withContext AppResult.Success(localCountry)
        }

        return@withContext try {
            val country = remoteDataSource.getCountry(code).toDomain()
            val countryEntity = country.toEntity()
            if (countryEntity != null) {
                localDataSource.upsertCountry(
                    item = countryEntity,
                    updatedAtMillis = System.currentTimeMillis()
                )
                AppResult.Success(country)
            } else {
                AppResult.Error(AppError.InvalidData)
            }
        } catch (throwable: Throwable) {
            if (localCountry != null) {
                AppResult.Success(localCountry)
            } else {
                AppResult.Error(throwable.toDataError().toAppError())
            }
        }
    }

    override suspend fun getCountryDetails(code: String): AppResult<CountryDetails> = withContext(ioDispatcher) {
        val localDetails = localDataSource.getCountryDetails(code)
        if (localDetails != null && cacheValidator.isDetailsValid(code)) {
            val (details, borderCodes) = localDetails
            val borderCountries = localDataSource.getCountriesByCodes(borderCodes).map { it.toDomain() }

            return@withContext AppResult.Success(
                details.copy(borders = buildBorders(borderCodes, borderCountries))
            )
        }

        return@withContext try {
            val details = remoteDataSource.getCountryDetails(code).toDomain()

            localDataSource.upsertCountryDetails(
                code = code,
                details = details,
                borderCodes = details.borders?.codes.orEmpty(),
                updatedAtMillis = System.currentTimeMillis()
            )

            AppResult.Success(details)
        } catch (throwable: Throwable) {
            if (localDetails != null) {
                val (details, borderCodes) = localDetails
                return@withContext AppResult.Success(
                    details.copy(borders = buildBorders(borderCodes, null))
                )
            } else {
                AppResult.Error(throwable.toDataError().toAppError())
            }
        }
    }

    private suspend fun replaceAllLocalCountries(countries: List<Country>) {
        localDataSource.replaceAllCountries(
            items = countries.mapNotNull { it.toEntity() },
            updatedAtMillis = System.currentTimeMillis()
        )
    }

    private suspend fun getAllRemoteCountries(): List<Country> {
        return remoteDataSource.getAllCountries()
            .map { it.toDomain() }
            .filter { it.name != null }
    }

    private fun buildBorders(codes: List<String>, countries: List<Country>?): Border? {
        return if (codes.isNotEmpty()) {
            Border(codes = codes, countries = countries)
        } else {
            null
        }
    }
}

internal class CacheValidator(
    private val localDataSource: CountryLocalDataSource,
    private val cacheTtlMillis: Long,
    private val clock: () -> Long = System::currentTimeMillis
) {
    suspend fun isAllCountriesValid(): Boolean {
        val updatedAt = localDataSource.getAllCountriesLastUpdatedAtMillis() ?: return false
        return (clock() - updatedAt) < cacheTtlMillis
    }

    suspend fun isCountryValid(code: String): Boolean {
        val updatedAt = localDataSource.getCountryLastUpdatedAtMillis(code) ?: return false
        return (clock() - updatedAt) < cacheTtlMillis
    }

    suspend fun isDetailsValid(code: String): Boolean {
        val updatedAt = localDataSource.getCountryDetailsLastUpdatedAtMillis(code) ?: return false
        return (clock() - updatedAt) < cacheTtlMillis
    }
}