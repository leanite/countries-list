package com.github.leanite.countries.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.leanite.countries.core.data.model.local.CacheMetadataEntity
import com.github.leanite.countries.core.data.model.local.CountryDetailsEntity
import com.github.leanite.countries.core.data.model.local.CountryEntity

@Database(
    entities = [CountryEntity::class, CacheMetadataEntity::class, CountryDetailsEntity::class],
    version = 3,
    exportSchema = false
)
internal abstract class CountriesDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDAO
    abstract fun countryDetailsDao(): CountryDetailsDAO
    abstract fun cacheMetadataDao(): CacheMetadataDAO
}
