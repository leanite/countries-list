package com.github.leanite.countries.core.domain.model

data class Continent(
    val key: String,
    val displayName: String
) {
    companion object {
        private val displayNameMap: Map<String, String> = mapOf(
            "Africa" to "África",
            "Antarctica" to "Antártida",
            "Asia" to "Ásia",
            "Europe" to "Europa",
            "North America" to "América do Norte",
            "Central America" to "América Central",
            "South America" to "América do Sul",
            "Oceania" to "Oceania"
        )

        fun fromKey(rawKey: String): Continent {
            val normalizedKey = rawKey.trim()
            val displayName = displayNameMap[normalizedKey] ?: normalizedKey
            return Continent(key = normalizedKey, displayName = displayName)
        }

        fun fromKeys(rawKeys: List<String>?): List<Continent>? {
            val keys = rawKeys
                .orEmpty()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .distinct()

            if (keys.isEmpty()) return null
            return keys.map(::fromKey)
        }
    }
}
