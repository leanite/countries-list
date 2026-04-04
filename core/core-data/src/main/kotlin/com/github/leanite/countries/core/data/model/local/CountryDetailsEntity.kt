package com.github.leanite.countries.core.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country_details")
internal data class CountryDetailsEntity(
    @PrimaryKey
    val code: String,
    val name: String,
    val officialName: String?,
    val capitalsJson: String?,
    val region: String?,
    val timezonesJson: String?,
    val area: Double?,
    val population: Long?,
    val currenciesJson: String?,
    val bordersJson: String?,
    val flagUrl: String?,
)
