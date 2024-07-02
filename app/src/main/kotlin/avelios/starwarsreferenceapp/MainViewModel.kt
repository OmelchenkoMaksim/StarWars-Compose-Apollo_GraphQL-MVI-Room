package avelios.starwarsreferenceapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class MainViewModel(
    private val actor: MainActor,
    private val networkManager: NetworkManager,
    internal val settingsManager: SettingsManager
) : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MainEffect>()

    private val _news = MutableSharedFlow<MainNews>()

    private val _selectedCharacter = MutableStateFlow<StarWarsCharacter?>(null)
    val selectedCharacter: StateFlow<StarWarsCharacter?> = _selectedCharacter

    private val _selectedStarship = MutableStateFlow<Starship?>(null)
    val selectedStarship: StateFlow<Starship?> = _selectedStarship

    private val _selectedPlanet = MutableStateFlow<Planet?>(null)
    val selectedPlanet: StateFlow<Planet?> = _selectedPlanet

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isDarkTheme = MutableStateFlow(settingsManager.isDarkMode())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private val _favoriteCharacters = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val favoriteCharacters: StateFlow<Map<String, Boolean>> = _favoriteCharacters.asStateFlow()

    val isNetworkAvailable: StateFlow<Boolean> = networkManager.isNetworkAvailable

    private val _charactersPager = MutableStateFlow(createCharactersPager())
    val charactersPager: StateFlow<Flow<PagingData<StarWarsCharacter>>> = _charactersPager

    private val _starshipsPager = MutableStateFlow(createStarshipsPager())
    val starshipsPager: StateFlow<Flow<PagingData<Starship>>> = _starshipsPager

    private val _planetsPager = MutableStateFlow(createPlanetsPager())
    val planetsPager: StateFlow<Flow<PagingData<Planet>>> = _planetsPager

    init {
        viewModelScope.launch {
            loadFavoriteCharacters()
            loadData()
        }

        viewModelScope.launch {
            isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) {
                    if (areListsEmpty()) {
                        refreshData()
                    } else {
                        _charactersPager.value = createCharactersPager()
                        loadData()
                    }
                } else {
                    _charactersPager.value = createLocalCharactersPager()
                    _starshipsPager.value = createLocalStarshipsPager()
                    _planetsPager.value = createLocalPlanetsPager()
                }
            }
        }
    }

    fun handleIntent(intent: MainIntent) {
        viewModelScope.launch {
            when (intent) {
                is MainIntent.LoadData -> loadData()
                is MainIntent.UpdateFavoriteStatus -> updateFavoriteStatus(intent.characterId, intent.isFavorite)
                is MainIntent.FetchCharacterDetails -> fetchCharacterDetails(intent.characterId)
                is MainIntent.FetchStarshipDetails -> fetchStarshipDetails(intent.starshipId)
                is MainIntent.FetchPlanetDetails -> fetchPlanetDetails(intent.planetId)
                is MainIntent.RefreshData -> refreshData()
            }
        }
    }

    internal suspend fun loadData() {
        _isLoading.value = true
        try {
            when (val result = actor.loadData()) {
                is MainState.DataLoaded -> {
                    if (result.characters.isEmpty() && result.starships.isEmpty() && result.planets.isEmpty()) {
                        _state.value = MainState.EmptyData
                    } else {
                        _state.value = result
                    }
                }

                is MainState.Error -> {
                    if (!networkManager.isNetworkAvailable.value && areLocalDataEmpty()) {
                        _state.value = MainState.NoInternetAndEmptyData
                    } else {
                        _state.value = result
                    }
                }

                else -> _state.value = result
            }
            _news.emit(MainNews.DataLoaded)
        } catch (e: Exception) {
            _news.emit(MainNews.ErrorOccurred(e.message ?: "Unknown error occurred"))
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun areLocalDataEmpty(): Boolean {
        return actor.areLocalDataEmpty()
    }

    private suspend fun loadFavoriteCharacters() {
        try {
            val favorites = actor.loadFavoriteCharacters()
            _favoriteCharacters.value = favorites
        } catch (e: Exception) {
            _news.emit(MainNews.ErrorOccurred("Failed to load favorite characters"))
        }
    }

    internal suspend fun updateFavoriteStatus(characterId: String, isFavorite: Boolean) {
        try {
            actor.updateFavoriteStatus(characterId, isFavorite)
            val updatedFavorites = _favoriteCharacters.value.toMutableMap()
            updatedFavorites[characterId] = isFavorite
            _favoriteCharacters.value = updatedFavorites
            _effect.emit(MainEffect.ShowToast(if (isFavorite) ADDED_TO_FAVORITES else REMOVED_FROM_FAVORITES))
        } catch (e: Exception) {
            _news.emit(MainNews.ErrorOccurred("Failed to update favorite status"))
        }
    }

    private suspend fun fetchCharacterDetails(characterId: String) {
        _isLoading.value = true
        try {
            val character = actor.fetchCharacterDetails(characterId)
            _selectedCharacter.value = character
            _effect.emit(MainEffect.NavigateToDetails(characterId, CHARACTER_TYPE))
        } catch (e: Exception) {
            _news.emit(MainNews.ErrorOccurred("Failed to fetch character details"))
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun fetchStarshipDetails(starshipId: String) {
        _isLoading.value = true
        try {
            val starship = actor.fetchStarshipDetails(starshipId)
            _selectedStarship.value = starship
            _effect.emit(MainEffect.NavigateToDetails(starshipId, STARSHIP_TYPE))
        } catch (e: Exception) {
            _news.emit(MainNews.ErrorOccurred("Failed to fetch starship details"))
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun fetchPlanetDetails(planetId: String) {
        _isLoading.value = true
        try {
            val planet = actor.fetchPlanetDetails(planetId)
            _selectedPlanet.value = planet
            _effect.emit(MainEffect.NavigateToDetails(planetId, PLANET_TYPE))
        } catch (e: Exception) {
            _news.emit(MainNews.ErrorOccurred("Failed to fetch planet details"))
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun refreshData() {
        _isLoading.value = true
        try {
            val newState = actor.refreshData()
            _state.value = newState
            _news.emit(MainNews.DataRefreshed)
        } catch (e: Exception) {
            _news.emit(MainNews.ErrorOccurred("Failed to refresh data"))
        } finally {
            _isLoading.value = false
        }
    }

    fun toggleTheme() {
        val newTheme = !_isDarkTheme.value
        _isDarkTheme.value = newTheme
        settingsManager.setDarkMode(newTheme)
        viewModelScope.launch { _effect.emit(MainEffect.ThemeChanged(newTheme)) }
    }

    private fun areListsEmpty(): Boolean {
        val currentState = _state.value
        return if (currentState is MainState.DataLoaded) {
            currentState.characters.isEmpty() && currentState.starships.isEmpty() && currentState.planets.isEmpty()
        } else true
    }

    private fun createCharactersPager(): Flow<PagingData<StarWarsCharacter>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { CharacterPagingSource(actor, favoriteCharacters) }
        ).flow.cachedIn(viewModelScope)
    }

    private fun createStarshipsPager(): Flow<PagingData<Starship>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { StarshipPagingSource(actor) }
        ).flow.cachedIn(viewModelScope)
    }

    private fun createPlanetsPager(): Flow<PagingData<Planet>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { PlanetPagingSource(actor) }
        ).flow.cachedIn(viewModelScope)
    }

    private fun createLocalCharactersPager(): Flow<PagingData<StarWarsCharacter>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { actor.getCharacterDao().getCharactersPagingSource() }
        ).flow.cachedIn(viewModelScope)
    }

    private fun createLocalStarshipsPager(): Flow<PagingData<Starship>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { actor.getStarshipDao().getStarshipsPagingSource() }
        ).flow.cachedIn(viewModelScope)
    }

    private fun createLocalPlanetsPager(): Flow<PagingData<Planet>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { actor.getPlanetDao().getPlanetsPagingSource() }
        ).flow.cachedIn(viewModelScope)
    }

    internal companion object {
        private const val ADDED_TO_FAVORITES = "Added to favorites"
        private const val REMOVED_FROM_FAVORITES = "Removed from favorites"
        private const val CHARACTER_TYPE = "character"
        private const val STARSHIP_TYPE = "starship"
        private const val PLANET_TYPE = "planet"
        internal const val PAGE_SIZE = 20
    }
}
