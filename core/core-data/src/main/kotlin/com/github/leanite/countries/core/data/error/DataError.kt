package com.github.leanite.countries.core.data.error

internal sealed interface DataError {
    data object Network : DataError
    data class Http(val code: Int) : DataError
    data object Serialization : DataError
    data class Unknown(val cause: Throwable) : DataError
}