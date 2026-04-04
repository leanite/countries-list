package com.github.leanite.countries.core.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
internal data class CountryEntity(
    @PrimaryKey
    val code: String,
    val name: String,
    val flagUrl: String?,
    val latitude: Double?,
    val longitude: Double?,
    val region: String?,
    val area: Double?,
)
