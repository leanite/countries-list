package com.github.leanite.countries.core.domain.usecase

import com.github.leanite.countries.core.domain.model.CountryDetails
import com.github.leanite.countries.core.domain.repository.CountryRepository
import com.github.leanite.countries.core.domain.result.AppResult
import javax.inject.Inject

class GetCountryDetailsUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(code: String): AppResult<CountryDetails> {
        return repository.getCountryDetails(code)
    }
}
