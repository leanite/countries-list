package com.github.leanite.countries.core.data.datasource

import androidx.room.withTransaction
import com.github.leanite.countries.core.data.local.CountriesDatabase
import com.github.leanite.countries.core.data.mapper.getBorderCodes
import com.github.leanite.countries.core.data.mapper.toDomainWithoutBorders
import com.github.leanite.countries.core.data.mapper.toEntity
import com.github.leanite.countries.core.data.model.local.CacheMetadataEntity
import com.github.leanite.countries.core.data.model.local.CountryEntity
import com.github.leanite.countries.core.domain.model.CountryDetails
import javax.inject.Inject

private const val COUNTRIES_CACHE_KEY = "countries_all"
private fun countryCacheKey(code: String) = "country_$code"
private fun detailsCacheKey(code: String) = "country_details_$code"


internal interface CountryLocalDataSource {
    suspend fun getAllCountries(): List<CountryEntity>
    suspend fun getCountryByCode(code: String): CountryEntity?
    suspend fun getCountriesByCodes(codes: List<String>): List<CountryEntity>
    suspend fun replaceAllCountries(items: List<CountryEntity>, updatedAtMillis: Long)
    suspend fun upsertCountry(item: CountryEntity, updatedAtMillis: Long)
    suspend fun getAllCountriesLastUpdatedAtMillis(): Long?
    suspend fun getCountryLastUpdatedAtMillis(code: String): Long?

    suspend fun getCountryDetails(code: String): Pair<CountryDetails, List<String>>?
    suspend fun upsertCountryDetails(code: String, details: CountryDetails, borderCodes: List<String>, updatedAtMillis: Long)
    suspend fun getCountryDetailsLastUpdatedAtMillis(code: String): Long?
}

internal class CountryLocalDataSourceImpl @Inject constructor(
    private val db: CountriesDatabase
) : CountryLocalDataSource {
    private val countryDao = db.countryDao()
    private val detailsDao = db.countryDetailsDao()
    private val metadataDao = db.cacheMetadataDao()

    override suspend fun getAllCountries(): List<CountryEntity> = countryDao.getAll()

    override suspend fun getCountryByCode(code: String): CountryEntity? = countryDao.getByCode(code)

    override suspend fun getCountriesByCodes(codes: List<String>): List<CountryEntity> {
        if (codes.isEmpty()) return emptyList()
        return countryDao.getByCodes(codes)
    }

    override suspend fun replaceAllCountries(items: List<CountryEntity>, updatedAtMillis: Long) {
        db.withTransaction {
            countryDao.clearAll()
            countryDao.upsertAll(items)
            metadataDao.upsert(
                CacheMetadataEntity(
                    key = COUNTRIES_CACHE_KEY,
                    updatedAtMillis = updatedAtMillis
                )
            )
        }
    }

    override suspend fun upsertCountry(item: CountryEntity, updatedAtMillis: Long) {
        countryDao.upsert(item)
        metadataDao.upsert(
            CacheMetadataEntity(
                key = countryCacheKey(item.code),
                updatedAtMillis = updatedAtMillis
            )
        )
    }

    override suspend fun getAllCountriesLastUpdatedAtMillis(): Long? {
        return metadataDao.get(COUNTRIES_CACHE_KEY)?.updatedAtMillis
    }

    override suspend fun getCountryLastUpdatedAtMillis(code: String): Long? {
        return metadataDao.get(countryCacheKey(code))?.updatedAtMillis
    }

    override suspend fun getCountryDetails(code: String): Pair<CountryDetails, List<String>>? {
        val entity = detailsDao.getByCode(code) ?: return null
        return entity.toDomainWithoutBorders() to entity.getBorderCodes()
    }

    override suspend fun upsertCountryDetails(
        code: String,
        details: CountryDetails,
        borderCodes: List<String>,
        updatedAtMillis: Long
    ) {
        db.withTransaction {
            detailsDao.upsert(details.toEntity(code = code, borderCodes = borderCodes))
            metadataDao.upsert(CacheMetadataEntity(detailsCacheKey(code), updatedAtMillis))
        }
    }

    override suspend fun getCountryDetailsLastUpdatedAtMillis(code: String): Long? {
        return metadataDao.get(detailsCacheKey(code))?.updatedAtMillis
    }
}