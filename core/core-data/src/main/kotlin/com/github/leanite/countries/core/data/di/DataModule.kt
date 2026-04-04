package com.github.leanite.countries.core.data.di

import com.github.leanite.countries.core.data.datasource.CountryLocalDataSource
import com.github.leanite.countries.core.data.datasource.CountryLocalDataSourceImpl
import com.github.leanite.countries.core.data.datasource.CountryRemoteDataSource
import com.github.leanite.countries.core.data.datasource.CountryRemoteDataSourceImpl
import com.github.leanite.countries.core.data.repository.CountryRepositoryImpl
import com.github.leanite.countries.core.domain.repository.CountryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindCountryRepository(
        impl: CountryRepositoryImpl
    ): CountryRepository

    @Binds
    abstract fun bindCountryRemoteDataSource(
        impl: CountryRemoteDataSourceImpl
    ): CountryRemoteDataSource

    @Binds
    abstract fun bindCountryLocalDataSource(
        impl: CountryLocalDataSourceImpl
    ): CountryLocalDataSource
}