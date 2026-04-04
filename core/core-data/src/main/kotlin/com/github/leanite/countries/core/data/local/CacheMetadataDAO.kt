package com.github.leanite.countries.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.leanite.countries.core.data.model.local.CacheMetadataEntity

@Dao
internal interface CacheMetadataDAO {
    @Query("SELECT * FROM cache_metadata WHERE `key` = :key LIMIT 1")
    suspend fun get(key: String): CacheMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CacheMetadataEntity)
}