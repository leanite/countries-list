package com.github.leanite.countries.core.domain.usecase

import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.repository.CountryRepository
import com.github.leanite.countries.core.domain.result.AppResult
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetBorderCountriesUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(codes: List<String>): AppResult<List<Country>> = coroutineScope {
        val uniqueCodes = codes.distinct()
        if (uniqueCodes.isEmpty()) return@coroutineScope AppResult.Success(emptyList())

        val semaphore = Semaphore(permits = 4)
        val channel = Channel<AppResult<Country>>(capacity = uniqueCodes.size)
        val workers = uniqueCodes.map { code ->
            async {
                semaphore.withPermit {
                    channel.send(repository.getCountry(code))
                }
            }
        }

        val countries = mutableListOf<Country>()
        repeat(times = uniqueCodes.size) {
            when (val result = channel.receive()) {
                is AppResult.Success -> countries += result.data
                is AppResult.Error -> {
                    workers.forEach { it.cancel(CancellationException("Fail-fast on first error")) }
                    channel.close()
                    return@coroutineScope AppResult.Error(result.error)
                }
            }
        }

        channel.close()
        AppResult.Success(countries)
    }
}
