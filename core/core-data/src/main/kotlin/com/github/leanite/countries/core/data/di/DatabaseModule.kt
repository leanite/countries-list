package com.github.leanite.countries.core.data.di

import android.content.Context
import androidx.room.Room
import com.github.leanite.countries.core.data.local.CountriesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CountriesDatabase {
        return Room.databaseBuilder(
            context,
            CountriesDatabase::class.java,
            "countries.db"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    @Named("cache_ttl_ms")
    fun provideCountriesCacheTtlMillis(): Long = 2 * 60 * 1000L
}
