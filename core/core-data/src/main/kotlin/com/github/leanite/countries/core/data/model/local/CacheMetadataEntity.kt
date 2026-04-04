package com.github.leanite.countries.core.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_metadata")
internal data class CacheMetadataEntity(
    @PrimaryKey
    val key: String,
    val updatedAtMillis: Long
)