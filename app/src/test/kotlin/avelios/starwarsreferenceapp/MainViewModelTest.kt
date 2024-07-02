package avelios.starwarsreferenceapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.mvi.MainActor
import avelios.starwarsreferenceapp.mvi.MainState
import avelios.starwarsreferenceapp.util.NetworkManager
import avelios.starwarsreferenceapp.viewmodel.MainViewModel
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var actor: MainActor

    @Mock
    private lateinit var networkManager: NetworkManager

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        setupMocks()
        viewModel = MainViewModel(actor, networkManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    private fun setupMocks() {
        whenever(actor.favoriteCharacters).thenReturn(MutableStateFlow(emptyMap()))
        whenever(actor.charactersPager).thenReturn(MutableStateFlow(emptyFlow()))
        whenever(actor.starshipsPager).thenReturn(MutableStateFlow(emptyFlow()))
        whenever(actor.planetsPager).thenReturn(MutableStateFlow(emptyFlow()))
        whenever(actor.selectedCharacter).thenReturn(MutableStateFlow(null))
        whenever(actor.selectedStarship).thenReturn(MutableStateFlow(null))
        whenever(actor.selectedPlanet).thenReturn(MutableStateFlow(null))
        whenever(actor.isLoading).thenReturn(MutableStateFlow(false))
        whenever(actor.state).thenReturn(MutableStateFlow(MainState.Loading))
        whenever(networkManager.isNetworkAvailable).thenReturn(MutableStateFlow(false))
    }

    @Test
    fun `test initial state is loading`() = runTest {
        assertEquals(MainState.Loading, viewModel.state.first())
    }

    @Test
    fun `test network availability`() = runTest {
        val networkStateFlow = MutableStateFlow(false)
        whenever(networkManager.isNetworkAvailable).thenReturn(networkStateFlow)

        networkStateFlow.value = true

        val job = launch {
            viewModel.isNetworkAvailable.collect {
                assertTrue(it)
            }
        }

        job.cancel()
    }

    @Test
    fun `test characters pager flow`() = runTest {
        whenever(actor.charactersPager).thenReturn(MutableStateFlow(emptyFlow()))

        val result = viewModel.charactersPager.first()
        assertEquals(emptyFlow<PagingData<StarWarsCharacter>>(), result)
    }
}
