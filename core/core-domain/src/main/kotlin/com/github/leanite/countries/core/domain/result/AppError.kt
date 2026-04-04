package com.github.leanite.countries.core.domain.result

sealed interface AppError {
    data object NoInternet : AppError
    data object Unauthorized : AppError
    data object ServerError : AppError
    data object NotFound : AppError
    data object InvalidData : AppError
    data object Unknown : AppError
}