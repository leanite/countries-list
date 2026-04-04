package com.github.leanite.countries.core.data.mapper

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CurrencyMapperTest {

    @Test
    fun `should return null when input is null`() {
        val result = mapCurrencies(null)

        assertNull(result)
    }

    @Test
    fun `should return null when json is empty`() {
        val result = mapCurrencies(buildJsonObject { })

        assertNull(result)
    }

    @Test
    fun `should map single currency with name`() {
        val json = buildJsonObject {
            put("BRL", buildJsonObject {
                put("name", JsonPrimitive("Brazilian real"))
            })
        }

        val result = mapCurrencies(json)

        assertEquals(1, result?.size)
        assertEquals("BRL", result?.first()?.id)
        assertEquals("Brazilian real", result?.first()?.name)
    }

    @Test
    fun `should map multiple currencies`() {
        val json = buildJsonObject {
            put("USD", buildJsonObject {
                put("name", JsonPrimitive("United States dollar"))
            })
            put("EUR", buildJsonObject {
                put("name", JsonPrimitive("Euro"))
            })
        }

        val result = mapCurrencies(json)

        assertEquals(2, result?.size)
    }

    @Test
    fun `should map currency with null name`() {
        val json = buildJsonObject {
            put("XXX", buildJsonObject { })
        }

        val result = mapCurrencies(json)

        assertEquals(1, result?.size)
        assertEquals("XXX", result?.first()?.id)
        assertNull(result?.first()?.name)
    }
}
