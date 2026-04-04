package com.github.leanite.countries.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.leanite.countries.core.data.model.local.CountryDetailsEntity

@Dao
internal interface CountryDetailsDAO {
    @Query("SELECT * FROM country_details WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): CountryDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CountryDetailsEntity)
}
