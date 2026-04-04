package com.github.leanite.countries.feature.list.usecase

import javax.inject.Inject
import kotlin.math.ln

class CalculateCountryTargetZoomUseCase @Inject constructor() {
    operator fun invoke(areaKm2: Double?): Float {
        if (areaKm2 == null || areaKm2 <= 0.0) return DEFAULT_TARGET_ZOOM
        val raw = REFERENCE_ZOOM - ZOOM_AREA_SCALE * ln(areaKm2 / REFERENCE_AREA_KM2) + GLOBAL_ZOOM_BIAS
        return raw.toFloat().coerceIn(MIN_TARGET_ZOOM, MAX_TARGET_ZOOM)
    }

    private companion object {
        private const val REFERENCE_AREA_KM2 = 92_212.0
        private const val REFERENCE_ZOOM = 6.3      // antes 8.5
        private const val ZOOM_AREA_SCALE = 0.45    // antes 0.66
        private const val MIN_TARGET_ZOOM = 3.2f    // antes 4.8
        private const val MAX_TARGET_ZOOM = 7.2f    // antes 10.8
        private const val DEFAULT_TARGET_ZOOM = 5.8f
        private const val GLOBAL_ZOOM_BIAS = -0.4   // ajuste fino glo
    }
}