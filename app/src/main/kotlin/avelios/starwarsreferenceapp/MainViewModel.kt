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

    val charactersPager: Flow<PagingData<StarWarsCharacter>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { CharacterPagingSource(actor, favoriteCharacters) }
    ).flow.cachedIn(viewModelScope)

    val starshipsPager: Flow<PagingData<Starship>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { StarshipPagingSource(actor) }
    ).flow.cachedIn(viewModelScope)

    val planetsPager: Flow<PagingData<Planet>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { PlanetPagingSource(actor) }
    ).flow.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            loadFavoriteCharacters()
            loadData()
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

    private suspend fun loadData() {
        _isLoading.value = true
        try {
            val data = actor.loadData()
            _state.value = data as MainState.DataLoaded
            _news.emit(MainNews.DataLoaded)
        } catch (e: Exception) {
            _news.emit(MainNews.ErrorOccurred(e.message ?: "Unknown error occurred"))
        } finally {
            _isLoading.value = false
        }
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

    internal suspend fun refreshData() {
        _isLoading.value = true
        try {
            actor.refreshData()
            loadData()
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
        viewModelScope.launch {
            _effect.emit(MainEffect.ThemeChanged(newTheme))
        }
    }

    fun areListsEmpty(): Boolean {
        val currentState = _state.value
        return if (currentState is MainState.DataLoaded) {
            currentState.characters.isEmpty() && currentState.starships.isEmpty() && currentState.planets.isEmpty()
        } else true
    }

    internal companion object {
        private const val ADDED_TO_FAVORITES = "Added to favorites"
        private const val REMOVED_FROM_FAVORITES = "Removed from favorites"
        private const val CHARACTER_TYPE = "character"
        private const val STARSHIP_TYPE = "starship"
        private const val PLANET_TYPE = "planet"
        private const val PAGE_SIZE = 10
    }
}
