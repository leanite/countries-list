package com.github.leanite.countries.feature.details

import app.cash.turbine.test
import com.github.leanite.countries.core.domain.result.AppError
import com.github.leanite.countries.core.domain.result.AppResult
import com.github.leanite.countries.feature.details.fixtures.argentinaCountry
import com.github.leanite.countries.feature.details.fixtures.brazilCountry
import com.github.leanite.countries.feature.details.fixtures.brazilCountryDetails
import com.github.leanite.countries.feature.details.fixtures.brazilCountryDetailsWithBorders
import com.github.leanite.countries.core.domain.usecase.GetBorderCountriesUseCase
import com.github.leanite.countries.core.domain.usecase.GetCountryDetailsUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryDetailsViewModelTest {

    @MockK
    lateinit var getCountryDetailsUseCase: GetCountryDetailsUseCase

    @MockK
    lateinit var getBorderCountriesUseCase: GetBorderCountriesUseCase

    private lateinit var viewModel: CountryDetailsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = CountryDetailsViewModel(
        getCountryDetailsUseCase = getCountryDetailsUseCase,
        getBorderCountriesUseCase = getBorderCountriesUseCase
    )

    @Test
    fun `initial state should be Idle`() {
        viewModel = createViewModel()
        assertEquals(CountryDetailsContentState.Idle, viewModel.uiState.value.contentState)
    }

    @Test
    fun `Load should fetch details and set Success state`() = runTest {
        coEvery { getCountryDetailsUseCase("BRA") } returns AppResult.Success(brazilCountryDetails)
        viewModel = createViewModel()

        viewModel.onAction(CountryDetailsAction.Load("BRA"))

        val state = viewModel.uiState.value
        assertEquals(CountryDetailsContentState.Success, state.contentState)
        assertEquals("BRA", state.countryCode)
        assertEquals("Brasil", state.countryDetails?.name)
    }

    @Test
    fun `Load with error should set Error state and emit ShowMessage`() = runTest {
        coEvery { getCountryDetailsUseCase("BRA") } returns AppResult.Error(AppError.NoInternet)
        viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onAction(CountryDetailsAction.Load("BRA"))

            assertEquals(CountryDetailsContentState.Error, viewModel.uiState.value.contentState)
            assertNull(viewModel.uiState.value.countryDetails)

            val event = awaitItem()
            assertTrue(event is CountryDetailsEvent.ShowMessage)
            val message = (event as CountryDetailsEvent.ShowMessage).type
            assertTrue(message is CountryDetailsMessage.ApiError)
        }
    }

    @Test
    fun `Load should enrich borders when details have border codes`() = runTest {
        coEvery { getCountryDetailsUseCase("BRA") } returns AppResult.Success(brazilCountryDetailsWithBorders)
        coEvery { getBorderCountriesUseCase(listOf("ARG", "URY")) } returns AppResult.Success(
            listOf(argentinaCountry)
        )
        viewModel = createViewModel()

        viewModel.onAction(CountryDetailsAction.Load("BRA"))

        val state = viewModel.uiState.value
        assertEquals(CountryDetailsContentState.Success, state.contentState)
        assertEquals(listOf(argentinaCountry), state.countryDetails?.borders?.countries)
    }

    @Test
    fun `Load should emit BorderLoadFailed when border fetch fails`() = runTest {
        coEvery { getCountryDetailsUseCase("BRA") } returns AppResult.Success(brazilCountryDetailsWithBorders)
        coEvery { getBorderCountriesUseCase(any()) } returns AppResult.Error(AppError.ServerError)
        viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onAction(CountryDetailsAction.Load("BRA"))

            val state = viewModel.uiState.value
            assertEquals(CountryDetailsContentState.Success, state.contentState)
            assertNull(state.countryDetails?.borders?.countries)

            val event = awaitItem()
            assertTrue(event is CountryDetailsEvent.ShowMessage)
            val message = (event as CountryDetailsEvent.ShowMessage).type
            assertTrue(message is CountryDetailsMessage.BorderLoadFailed)
        }
    }

    @Test
    fun `Retry should reload details using stored countryCode`() = runTest {
        coEvery { getCountryDetailsUseCase("BRA") } returns AppResult.Error(AppError.NoInternet)
        viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onAction(CountryDetailsAction.Load("BRA"))
            awaitItem() // consume error event
        }

        coEvery { getCountryDetailsUseCase("BRA") } returns AppResult.Success(brazilCountryDetails)
        viewModel.onAction(CountryDetailsAction.Retry)

        val state = viewModel.uiState.value
        assertEquals(CountryDetailsContentState.Success, state.contentState)
        assertEquals("Brasil", state.countryDetails?.name)
    }

    @Test
    fun `Retry with blank countryCode should be ignored`() = runTest {
        viewModel = createViewModel()

        viewModel.onAction(CountryDetailsAction.Retry)

        assertEquals(CountryDetailsContentState.Idle, viewModel.uiState.value.contentState)
    }

    @Test
    fun `BorderCountryClick should emit NavigateToCountryDetails`() = runTest {
        viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onAction(CountryDetailsAction.BorderCountryClick("ARG"))

            val event = awaitItem()
            assertTrue(event is CountryDetailsEvent.NavigateToCountryDetails)
            assertEquals("ARG", (event as CountryDetailsEvent.NavigateToCountryDetails).countryCode)
        }
    }

    @Test
    fun `BorderCountryClick with null code should emit BorderCodeUnavailable`() = runTest {
        viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onAction(CountryDetailsAction.BorderCountryClick(null))

            val event = awaitItem()
            assertTrue(event is CountryDetailsEvent.ShowMessage)
            val message = (event as CountryDetailsEvent.ShowMessage).type
            assertTrue(message is CountryDetailsMessage.BorderCodeUnavailable)
        }
    }

    @Test
    fun `BorderCountryClick with blank code should emit BorderCodeUnavailable`() = runTest {
        viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onAction(CountryDetailsAction.BorderCountryClick("  "))

            val event = awaitItem()
            assertTrue(event is CountryDetailsEvent.ShowMessage)
            val message = (event as CountryDetailsEvent.ShowMessage).type
            assertTrue(message is CountryDetailsMessage.BorderCodeUnavailable)
        }
    }

    @Test
    fun `SeeOnMapClick should emit OpenOnMap with country code`() = runTest {
        coEvery { getCountryDetailsUseCase("BRA") } returns AppResult.Success(brazilCountryDetails)
        viewModel = createViewModel()
        viewModel.onAction(CountryDetailsAction.Load("BRA"))

        viewModel.events.test {
            viewModel.onAction(CountryDetailsAction.SeeOnMapClick)

            val event = awaitItem()
            assertTrue(event is CountryDetailsEvent.OpenOnMap)
            assertEquals("BRA", (event as CountryDetailsEvent.OpenOnMap).countryCode)
        }
    }

    @Test
    fun `SeeOnMapClick with blank countryCode should emit CountryCodeUnavailable`() = runTest {
        viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onAction(CountryDetailsAction.SeeOnMapClick)

            val event = awaitItem()
            assertTrue(event is CountryDetailsEvent.ShowMessage)
            val message = (event as CountryDetailsEvent.ShowMessage).type
            assertTrue(message is CountryDetailsMessage.CountryCodeUnavailable)
        }
    }

}
