package com.github.leanite.countries.core.domain.usecase

import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.repository.CountryRepository
import com.github.leanite.countries.core.domain.result.AppResult
import com.github.leanite.countries.core.domain.result.map
import com.github.leanite.countries.core.domain.extension.normalizedSortKey
import javax.inject.Inject

class GetAllCountriesUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(): AppResult<List<Country>> {
        return repository.getCountries().map { countries ->
            countries
                .mapNotNull { country ->
                    val trimmedName = country.name?.trim()

                    if (trimmedName.isNullOrBlank()) null
                    else country.copy(name = trimmedName)
                }
                .sortedBy { it.name.orEmpty().normalizedSortKey() }
        }
    }
}
