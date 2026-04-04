package com.github.leanite.countries.core.data.error

import java.io.IOException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

internal fun Throwable.toDataError(): DataError {
    return when (this) {
        is IOException -> DataError.Network
        is HttpException -> DataError.Http(code())
        is SerializationException -> DataError.Serialization
        else -> DataError.Unknown(this)
    }
}