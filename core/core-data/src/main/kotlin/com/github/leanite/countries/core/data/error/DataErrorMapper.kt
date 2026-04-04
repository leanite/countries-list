package com.github.leanite.countries.core.data.error

import com.github.leanite.countries.core.domain.error.AppError

internal fun DataError.toAppError(): AppError {
    return when (this) {
        DataError.Network -> AppError.NoInternet
        is DataError.Http -> when (code) {
            400 -> AppError.InvalidData
            401, 403 -> AppError.Unauthorized
            404 -> AppError.NotFound
            429, in 500..599 -> AppError.ServerError
            else -> AppError.Unknown
        }
        DataError.Serialization -> AppError.InvalidData
        is DataError.Unknown -> AppError.Unknown
    }
}