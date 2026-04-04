package com.github.leanite.countries.core.domain.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultSuccessTest {

    @Test
    fun `map should transform data when result is success`() {
        val source: AppResult<Int> = AppResult.Success(5)

        val mapped = source.map { it * 2 }

        assertTrue(mapped is AppResult.Success)
        assertEquals(10, (mapped as AppResult.Success).data)
    }

    @Test
    fun `map should allow type change`() {
        val source: AppResult<Int> = AppResult.Success(42)

        val mapped = source.map { it.toString() }

        assertTrue(mapped is AppResult.Success)
        assertEquals("42", (mapped as AppResult.Success).data)
    }
}
