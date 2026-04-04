package com.github.leanite.countries.feature.list

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.leanite.countries.core.bottomsheet.BottomSheetState
import com.github.leanite.countries.feature.list.ui.CountriesBottomSheet
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun CountriesListScreen(
    uiState: CountriesListUiState,
    cameraPositionState: CameraPositionState,
    onAction: (CountriesListAction) -> Unit,
) {
    val animatedMapHeightFraction =
        rememberAnimatedMapHeightFraction(uiState.bottomSheetState)

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = uiState.isMapNavigationEnabled,
                zoomGesturesEnabled = uiState.isMapNavigationEnabled,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(animatedMapHeightFraction)
                .align(Alignment.TopCenter),
        ) {
            val selectedCountry = uiState.selectedCountry
            val selectedLocation = selectedCountry?.location
            if (selectedLocation != null) {
                Marker(
                    state = MarkerState(
                        position = LatLng(selectedLocation.latitude, selectedLocation.longitude)
                    ),
                    title = selectedCountry.name ?: "País selecionado"
                )
            }
        }

        CountriesBottomSheet(
            uiState = uiState,
            onAction = onAction,
        )
    }
}

@Composable
private fun rememberAnimatedMapHeightFraction(bottomSheetState: BottomSheetState): Float {
    val mapHeightFraction = when (bottomSheetState) {
        BottomSheetState.Collapsed -> 0.60f
        BottomSheetState.Half,
        BottomSheetState.Expanded -> 0.40f
    }
    val fraction by animateFloatAsState(
        targetValue = mapHeightFraction,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "map-height-fraction"
    )
    return fraction
}
