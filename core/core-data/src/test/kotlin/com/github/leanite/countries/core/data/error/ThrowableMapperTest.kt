package com.github.leanite.countries.core.data.error

import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException

class ThrowableMapperTest {

    @Test
    fun `should map IOException to DataError Network`() {
        val result = IOException("no internet").toDataError()
        assertTrue(result is DataError.Network)
    }

    @Test
    fun `should map HttpException to DataError Http with same code`() {
        val exception = mockk<HttpException>()
        every { exception.code() } returns 404

        val result = exception.toDataError()

        assertTrue(result is DataError.Http)
        assertTrue((result as DataError.Http).code == 404)
    }

    @Test
    fun `should map SerializationException to DataError Serialization`() {
        val result = SerializationException("invalid json").toDataError()
        assertTrue(result is DataError.Serialization)
    }

    @Test
    fun `should map generic Exception to DataError Unknown`() {
        val source = IllegalStateException("generic error")

        val result = source.toDataError()

        assertTrue(result is DataError.Unknown)
        assertTrue((result as DataError.Unknown).cause === source)
    }
}
