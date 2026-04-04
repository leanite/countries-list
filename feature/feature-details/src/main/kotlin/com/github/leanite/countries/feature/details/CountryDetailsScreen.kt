package com.github.leanite.countries.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.github.leanite.countries.core.domain.model.CountryDetails
import com.github.leanite.countries.feature.details.extension.joinOrDash
import com.github.leanite.countries.feature.details.extension.orDash
import com.github.leanite.countries.feature.details.ui.CountryBorderItem
import com.github.leanite.countries.feature.details.ui.CountryDetailsFlagPlaceholder
import com.github.leanite.countries.feature.details.ui.preview.errorPreviewUiState
import com.github.leanite.countries.feature.details.ui.preview.loadingPreviewUiState
import com.github.leanite.countries.feature.details.ui.preview.successPreviewUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailsScreen(
    uiState: CountryDetailsUiState,
    onAction: (CountryDetailsAction) -> Unit,
    onBackClick: () -> Unit,
) {
    val title = uiState.countryDetails?.name?.takeIf { it.isNotBlank() } ?: "Detalhes do país"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(CountryDetailsAction.SeeOnMapClick) }) {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = "Ver no mapa"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState.contentState) {
            CountryDetailsContentState.Idle,
            CountryDetailsContentState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .testTag(CountryDetailsScreenTestTags.LOADING),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            CountryDetailsContentState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Não foi possível carregar os detalhes.")
                        Button(
                            onClick = { onAction(CountryDetailsAction.Retry) },
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            CountryDetailsContentState.Success -> {
                val details = uiState.countryDetails
                if (details == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        Text("Nenhum detalhe disponível.")
                    }
                } else {
                    CountryDetailsContent(
                        details = details,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        onBorderCountryClick = { code ->
                            onAction(CountryDetailsAction.BorderCountryClick(code))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CountryDetailsContent(
    details: CountryDetails,
    onBorderCountryClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val countryBorders = details.borders?.countries

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SubcomposeAsyncImage(
            model = details.flagUrl,
            contentDescription = details.name,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .aspectRatio(16f / 10f),
            contentScale = ContentScale.FillBounds,
            error = { CountryDetailsFlagPlaceholder() }
        )

        Surface(
            tonalElevation = 1.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow("Nome", details.name.orDash())
                DetailRow("Nome oficial", details.officialName.orDash())
                DetailRow("Capital", details.capitals.joinOrDash())
                DetailRow("Região", details.region.orDash())
            }
        }

        Surface(
            tonalElevation = 1.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow("Fusos horários", details.timezones.joinOrDash())
                DetailRow("Área", details.area?.let { "%,.0f km²".format(it) } ?: "-")
                DetailRow("População", details.population?.let { "%,d".format(it) } ?: "-")
                DetailRow("Moedas", details.currencies?.mapNotNull { it.name }.joinOrDash())
            }
        }

        if (!countryBorders.isNullOrEmpty()) {
            Text(
                text = "Fronteiras",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
            Surface(
                tonalElevation = 1.dp,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    countryBorders.forEach { country ->
                        CountryBorderItem(country = country, onClick = {
                            onBorderCountryClick(country.code?.value)
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

object CountryDetailsScreenTestTags {
    const val LOADING = "details_loading"
}

@Preview(showBackground = true)
@Composable
private fun CountryDetailsScreenLoadingPreview() {
    CountryDetailsScreen(
        uiState = loadingPreviewUiState,
        onAction = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun CountryDetailsScreenErrorPreview() {
    CountryDetailsScreen(
        uiState = errorPreviewUiState,
        onAction = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun CountryDetailsScreenSuccessPreview() {
    CountryDetailsScreen(
        uiState = successPreviewUiState,
        onAction = {},
        onBackClick = {}
    )
}
