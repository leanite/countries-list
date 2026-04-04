package com.github.leanite.countries.feature.list

import app.cash.turbine.test
import com.github.leanite.countries.core.bottomsheet.BottomSheetState
import com.github.leanite.countries.core.domain.result.AppError
import com.github.leanite.countries.core.domain.model.Country
import com.github.leanite.countries.core.domain.result.AppResult
import com.github.leanite.countries.feature.list.fixture.CountryFixtures.allCountries
import com.github.leanite.countries.feature.list.fixture.CountryFixtures.argentina
import com.github.leanite.countries.feature.list.fixture.CountryFixtures.brazil
import com.github.leanite.countries.feature.list.fixture.CountryFixtures.brazilWithNoCode
import com.github.leanite.countries.feature.list.fixture.CountryFixtures.brazilWithNoLocation
import com.github.leanite.countries.feature.list.fixture.CountryFixtures.emptySections
import com.github.leanite.countries.feature.list.fixture.CountryFixtures.germany
import com.github.leanite.countries.feature.list.ui.model.ALL_REGION_FILTER_KEY
import com.github.leanite.countries.feature.list.usecase.BuildCountrySectionsUseCase
import com.github.leanite.countries.core.domain.usecase.GetAllCountriesUseCase
import com.github.leanite.countries.feature.list.usecase.CalculateCountryTargetZoomUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountriesListViewModelTest {

    @MockK
    lateinit var getAllCountriesUseCase: GetAllCountriesUseCase

    @MockK
    lateinit var buildCountrySectionsUseCase: BuildCountrySectionsUseCase

    @MockK
    lateinit var calculateCountryTargetZoomUseCase: CalculateCountryTargetZoomUseCase

    private lateinit var viewModel: CountriesListViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        every { buildCountrySectionsUseCase(any()) } returns emptySections
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = CountriesListViewModel(
        getAllCountriesUseCase = getAllCountriesUseCase,
        buildCountrySectionsUseCase = buildCountrySectionsUseCase,
        calculateCountryTargetZoomUseCase = calculateCountryTargetZoomUseCase
    )

    private fun createLoadedViewModel(countries: List<Country> = allCountries): CountriesListViewModel {
        coEvery { getAllCountriesUseCase() } returns AppResult.Success(countries)
        return createViewModel().also { vm ->
            vm.onAction(CountriesListAction.Load)
        }
    }

    @Test
    fun `initial state should be Idle`() {
        viewModel = createViewModel()
        assertEquals(CountriesListContentState.Idle, viewModel.uiState.value.contentState)
    }

    @Test
    fun `Load should fetch countries and set Success state`() = runTest {
        viewModel = createLoadedViewModel()

        val state = viewModel.uiState.value
        assertEquals(CountriesListContentState.Success, state.contentState)
        assertEquals(allCountries, state.visibleCountries)
    }

    @Test
    fun `Load with error should set Error state and emit ShowMessage`() = runTest {
        coEvery { getAllCountriesUseCase() } returns AppResult.Error(AppError.NoInternet)
        viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onAction(CountriesListAction.Load)

            assertEquals(CountriesListContentState.Error, viewModel.uiState.value.contentState)

            val event = awaitItem()
            assertTrue(event is CountriesListEvent.ShowMessage)
            val message = (event as CountriesListEvent.ShowMessage).type
            assertTrue(message is CountriesListMessage.ApiError)
        }
    }

    @Test
    fun `duplicate Load after Success should be ignored`() = runTest {
        viewModel = createLoadedViewModel()

        val updatedCountries = listOf(brazil)
        coEvery { getAllCountriesUseCase() } returns AppResult.Success(updatedCountries)

        viewModel.onAction(CountriesListAction.Load)

        assertEquals(allCountries, viewModel.uiState.value.visibleCountries)
    }

    @Test
    fun `Retry should force reload even after Success`() = runTest {
        viewModel = createLoadedViewModel()

        val updatedCountries = listOf(brazil)
        coEvery { getAllCountriesUseCase() } returns AppResult.Success(updatedCountries)

        viewModel.onAction(CountriesListAction.Retry)

        assertEquals(updatedCountries, viewModel.uiState.value.visibleCountries)
    }

    @Test
    fun `SearchQueryChange should filter countries by name`() = runTest {
        viewModel = createLoadedViewModel()

        viewModel.onAction(CountriesListAction.SearchQueryChange("Bra"))

        val state = viewModel.uiState.value
        assertEquals("Bra", state.searchQuery)
        assertEquals(listOf(brazil), state.visibleCountries)
    }

    @Test
    fun `SearchQueryChange should be case insensitive`() = runTest {
        viewModel = createLoadedViewModel()

        viewModel.onAction(CountriesListAction.SearchQueryChange("brasil"))

        assertEquals(listOf(brazil), viewModel.uiState.value.visibleCountries)
    }

    @Test
    fun `blank search should show all countries`() = runTest {
        viewModel = createLoadedViewModel()

        viewModel.onAction(CountriesListAction.SearchQueryChange("Bra"))
        assertEquals(1, viewModel.uiState.value.visibleCountries.size)

        viewModel.onAction(CountriesListAction.SearchQueryChange(""))
        assertEquals(allCountries, viewModel.uiState.value.visibleCountries)
    }

    @Test
    fun `RegionFilterChange should update filter and filtered countries`() = runTest {
        viewModel = createLoadedViewModel()

        viewModel.onAction(CountriesListAction.RegionFilterChange("Americas"))

        val state = viewModel.uiState.value
        assertEquals("Americas", state.selectedRegionFilter)
        assertEquals(listOf(argentina, brazil), state.visibleCountries)
    }

    @Test
    fun `RegionFilterChange to same filter should be ignored`() = runTest {
        viewModel = createLoadedViewModel()

        viewModel.onAction(CountriesListAction.RegionFilterChange(ALL_REGION_FILTER_KEY))

        assertEquals(allCountries, viewModel.uiState.value.visibleCountries)
    }

    @Test
    fun `RegionFilterChange to Europe should show only European countries`() = runTest {
        viewModel = createLoadedViewModel()

        viewModel.onAction(CountriesListAction.RegionFilterChange("Europe"))

        assertEquals(listOf(germany), viewModel.uiState.value.visibleCountries)
    }

    @Test
    fun `CountryClick should set selectedCountry and emit camera event`() = runTest {
        every { calculateCountryTargetZoomUseCase(brazil.area) } returns 5.0f
        viewModel = createLoadedViewModel()

        viewModel.events.test {
            viewModel.onAction(CountriesListAction.CountryClick(brazil))

            assertEquals(brazil, viewModel.uiState.value.selectedCountry)

            val event = awaitItem()
            assertTrue(event is CountriesListEvent.MoveCameraToLocation)
            val cameraEvent = event as CountriesListEvent.MoveCameraToLocation
            assertEquals(brazil.location, cameraEvent.location)
            assertTrue(cameraEvent.fullAnimation)
        }
    }

    @Test
    fun `CountryClick with no location should emit ShowMessage`() = runTest {
        viewModel = createLoadedViewModel(listOf(brazilWithNoLocation))

        viewModel.events.test {
            viewModel.onAction(CountriesListAction.CountryClick(brazilWithNoLocation))

            val event = awaitItem()
            assertTrue(event is CountriesListEvent.ShowMessage)
        }
    }

    @Test
    fun `CountryDetailsClick should emit NavigateToCountryDetails`() = runTest {
        viewModel = createLoadedViewModel()

        viewModel.events.test {
            viewModel.onAction(CountriesListAction.CountryDetailsClick(brazil))

            val event = awaitItem()
            assertTrue(event is CountriesListEvent.NavigateToCountryDetails)
            assertEquals(
                "BRA",
                (event as CountriesListEvent.NavigateToCountryDetails).countryCode
            )
        }
    }

    @Test
    fun `CountryDetailsClick with no code should emit ShowMessage`() = runTest {
        viewModel = createLoadedViewModel(listOf(brazilWithNoCode))

        viewModel.events.test {
            viewModel.onAction(CountriesListAction.CountryDetailsClick(brazilWithNoCode))

            val event = awaitItem()
            assertTrue(event is CountriesListEvent.ShowMessage)
        }
    }

    @Test
    fun `BottomSheetStateChange should update state`() {
        viewModel = createLoadedViewModel()

        viewModel.onAction(
            CountriesListAction.BottomSheetStateChange(BottomSheetState.Expanded)
        )

        assertEquals(BottomSheetState.Expanded, viewModel.uiState.value.bottomSheetState)
    }

    @Test
    fun `FocusCountryFromNavigation should clear search and focus country`() = runTest {
        every { calculateCountryTargetZoomUseCase(brazil.area) } returns 5.0f
        viewModel = createLoadedViewModel()
        viewModel.onAction(CountriesListAction.SearchQueryChange("some query"))

        viewModel.events.test {
            viewModel.onAction(CountriesListAction.FocusCountryFromNavigation("BRA"))

            val state = viewModel.uiState.value
            assertEquals("", state.searchQuery)
            assertEquals(brazil, state.selectedCountry)
            assertEquals("BRA", state.focusCountryCode)

            val event = awaitItem()
            assertTrue(event is CountriesListEvent.MoveCameraToLocation)
            assertFalse((event as CountriesListEvent.MoveCameraToLocation).fullAnimation)
        }
    }

    @Test
    fun `FocusCountryFromNavigation with unknown code should emit ShowMessage`() = runTest {
        viewModel = createLoadedViewModel()

        viewModel.events.test {
            viewModel.onAction(CountriesListAction.FocusCountryFromNavigation("XYZ"))

            val event = awaitItem()
            assertTrue(event is CountriesListEvent.ShowMessage)
        }
    }

    @Test
    fun `FocusCountryFromNavigation with blank code should be ignored`() = runTest {
        viewModel = createLoadedViewModel()

        viewModel.onAction(CountriesListAction.FocusCountryFromNavigation("  "))

        assertNull(viewModel.uiState.value.selectedCountry)
    }

    @Test
    fun `FocusCountryHandled should clear focusCountryCode`() = runTest {
        every { calculateCountryTargetZoomUseCase(brazil.area) } returns 5.0f
        viewModel = createLoadedViewModel()

        viewModel.onAction(CountriesListAction.FocusCountryFromNavigation("BRA"))
        assertEquals("BRA", viewModel.uiState.value.focusCountryCode)

        viewModel.onAction(CountriesListAction.FocusCountryHandled)
        assertNull(viewModel.uiState.value.focusCountryCode)
    }

}
