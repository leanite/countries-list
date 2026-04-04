package com.github.leanite.countries.core.data.error

import com.github.leanite.countries.core.domain.error.AppError
import org.junit.Assert.assertEquals
import org.junit.Test

class DataErrorMapperTest {

    @Test
    fun `should map Network to NoInternet`() {
        val result = DataError.Network.toAppError()
        assertEquals(AppError.NoInternet, result)
    }

    @Test
    fun `should map Http 401 to Unauthorized`() {
        val result = DataError.Http(401).toAppError()
        assertEquals(AppError.Unauthorized, result)
    }

    @Test
    fun `should map Http 404 to NotFound`() {
        val result = DataError.Http(404).toAppError()
        assertEquals(AppError.NotFound, result)
    }

    @Test
    fun `should map Http 500 to ServerError`() {
        val result = DataError.Http(500).toAppError()
        assertEquals(AppError.ServerError, result)
    }

    @Test
    fun `should map unknown Http code to Unknown`() {
        val result = DataError.Http(418).toAppError()
        assertEquals(AppError.Unknown, result)
    }

    @Test
    fun `should map Serialization to InvalidData`() {
        val result = DataError.Serialization.toAppError()
        assertEquals(AppError.InvalidData, result)
    }

    @Test
    fun `should map Unknown to Unknown`() {
        val result = DataError.Unknown(RuntimeException("boom")).toAppError()
        assertEquals(AppError.Unknown, result)
    }
}
