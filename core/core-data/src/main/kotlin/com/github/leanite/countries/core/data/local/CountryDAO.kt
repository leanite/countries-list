package com.github.leanite.countries.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.leanite.countries.core.data.model.local.CountryEntity

@Dao
internal interface CountryDAO {
    @Query("SELECT * FROM countries ORDER BY name ASC")
    suspend fun getAll(): List<CountryEntity>

    @Query("SELECT * FROM countries WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): CountryEntity?

    @Query("SELECT * FROM countries WHERE code IN (:codes)")
    suspend fun getByCodes(codes: List<String>): List<CountryEntity>

    @Query("DELETE FROM countries")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CountryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CountryEntity>)
}