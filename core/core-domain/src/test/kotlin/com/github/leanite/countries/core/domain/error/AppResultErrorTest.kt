package com.github.leanite.countries.core.domain.error

import com.github.leanite.countries.core.domain.result.AppResult
import com.github.leanite.countries.core.domain.result.map
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultErrorTest {

    @Test
    fun `map should keep error when result is error`() {
        val source: AppResult<Int> = AppResult.Error(AppError.NoInternet)

        val mapped = source.map { it * 2 }

        assertTrue(mapped is AppResult.Error)
        assertEquals(AppError.NoInternet, (mapped as AppResult.Error).error)
    }

    @Test
    fun `map should not execute transform when result is error`() {
        val source: AppResult<Int> = AppResult.Error(AppError.InvalidData)
        var transformCalls = 0

        val mapped = source.map {
            transformCalls++
            it * 2
        }

        assertTrue(mapped is AppResult.Error)
        assertEquals(AppError.InvalidData, (mapped as AppResult.Error).error)
        assertEquals(0, transformCalls)
    }
}
