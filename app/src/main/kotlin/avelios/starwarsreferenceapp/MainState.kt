package avelios.starwarsreferenceapp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

internal sealed class MainAction {
    object LoadData : MainAction()
    data class UpdateFavoriteStatus(val characterId: String, val isFavorite: Boolean) : MainAction()
    data class FetchCharacterDetails(val characterId: String) : MainAction()
    data class FetchStarshipDetails(val starshipId: String) : MainAction()
    data class FetchPlanetDetails(val planetId: String) : MainAction()
    data class UpdateThemeAndTypography(
        val themeVariant: ThemeVariant,
        val typographyVariant: TypographyVariant
    ) : MainAction()

    object RefreshData : MainAction()
    object ToggleTheme : MainAction()
    object ToggleShowOnlyFavorites : MainAction()
}

internal sealed class MainState {
    object Loading : MainState()
    data class DataLoaded(
        val characters: List<StarWarsCharacter> = emptyList(),
        val starships: List<Starship> = emptyList(),
        val planets: List<Planet> = emptyList(),
        val isNetworkAvailable: Boolean = false,
        val favoriteCharacters: Map<String, Boolean> = emptyMap(),
        val themeVariant: ThemeVariant = ThemeVariant.MorningMystic,
        val typographyVariant: TypographyVariant = TypographyVariant.Classic,
        val isDarkTheme: Boolean = false,
        val showOnlyFavorites: Boolean = false
    ) : MainState()

    object EmptyData : MainState()
    data class ShowToast(val message: String) : MainState()
    object ThemeChanged : MainState()
    object NoInternetAndEmptyData : MainState()
    data class Error(val message: String) : MainState()
}

sealed class MainEffect {
    data class ShowToast(val message: String) : MainEffect()
    data class NavigateToDetails(val id: String, val type: String) : MainEffect()
    data class ThemeChanged(val isDarkTheme: Boolean) : MainEffect()
}

data class CharactersResponse(
    val characters: List<StarWarsCharacter>,
    val pageInfo: PageInfo
)

data class StarshipsResponse(
    val starships: List<Starship>,
    val pageInfo: PageInfo
)

data class PlanetsResponse(
    val planets: List<Planet>,
    val pageInfo: PageInfo
)

data class PageInfo(
    val endCursor: String?,
    val hasNextPage: Boolean
)

internal class MainActor(
    private val repository: StarWarsRepository,
    private val networkManager: NetworkManager,
    private val settingsManager: SettingsManager,
    private val coroutineScope: CoroutineScope
) {
    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MainEffect>()

    private val _favoriteCharacters = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val favoriteCharacters: StateFlow<Map<String, Boolean>> = _favoriteCharacters.asStateFlow()

    private val _charactersPager = MutableStateFlow(createCharactersPager())
    val charactersPager: StateFlow<Flow<PagingData<StarWarsCharacter>>> = _charactersPager

    private val _starshipsPager = MutableStateFlow(createStarshipsPager())
    val starshipsPager: StateFlow<Flow<PagingData<Starship>>> = _starshipsPager

    private val _planetsPager = MutableStateFlow(createPlanetsPager())
    val planetsPager: StateFlow<Flow<PagingData<Planet>>> = _planetsPager

    private val _selectedCharacter = MutableStateFlow<StarWarsCharacter?>(null)
    val selectedCharacter: StateFlow<StarWarsCharacter?> = _selectedCharacter.asStateFlow()

    private val _selectedStarship = MutableStateFlow<Starship?>(null)
    val selectedStarship: StateFlow<Starship?> = _selectedStarship.asStateFlow()

    private val _selectedPlanet = MutableStateFlow<Planet?>(null)
    val selectedPlanet: StateFlow<Planet?> = _selectedPlanet.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        _state.value = MainState.Loading
        Timber.d("MainActor initialized with Loading state")
        observeNetworkChanges()
    }

    suspend fun handleIntent(intent: MainAction) {
        when (intent) {
            is MainAction.LoadData -> loadData()
            is MainAction.UpdateFavoriteStatus -> updateFavoriteStatus(intent.characterId, intent.isFavorite)
            is MainAction.FetchCharacterDetails -> fetchCharacterDetails(intent.characterId)
            is MainAction.FetchStarshipDetails -> fetchStarshipDetails(intent.starshipId)
            is MainAction.FetchPlanetDetails -> fetchPlanetDetails(intent.planetId)
            is MainAction.RefreshData -> refreshData()
            is MainAction.ToggleTheme -> toggleTheme()
            is MainAction.ToggleShowOnlyFavorites -> toggleShowOnlyFavorites()
            is MainAction.UpdateThemeAndTypography -> updateThemeAndTypography(
                intent.themeVariant,
                intent.typographyVariant
            )
        }
    }

    private suspend fun updateThemeAndTypography(
        newThemeVariant: ThemeVariant,
        newTypographyVariant: TypographyVariant
    ) {
        settingsManager.saveThemeVariant(newThemeVariant)
        settingsManager.saveTypographyVariant(newTypographyVariant)
        _state.value = when (val currentState = _state.value) {
            is MainState.DataLoaded -> currentState.copy(
                themeVariant = newThemeVariant,
                typographyVariant = newTypographyVariant
            )

            else -> _state.value
        }
        _effect.emit(MainEffect.ThemeChanged(settingsManager.isDarkMode()))
    }

    private suspend fun loadData() {
        _isLoading.value = true
        Timber.d("Loading data started")
        try {
            val characters = repository.getAllCharacters()
            val starships = repository.getAllStarships()
            val planets = repository.getAllPlanets()
            if (characters.isEmpty() && starships.isEmpty() && planets.isEmpty()) {
                if (networkManager.isNetworkAvailable.value) {
                    Timber.d("Local data is empty, trying to fetch from network")
                    refreshData()
                } else {
                    Timber.d("Local data is empty and no network, showing NoInternetAndEmptyData state")
                    _state.value = MainState.NoInternetAndEmptyData
                    _effect.emit(MainEffect.ShowToast("No internet connection and no data available"))
                }
            } else {
                Timber.d("Data loaded successfully: ${characters.size} characters, ${starships.size} starships, ${planets.size} planets")
                _state.value = MainState.DataLoaded(
                    characters, starships, planets,
                    networkManager.isNetworkAvailable.value,
                    themeVariant = settingsManager.loadThemeVariant(),
                    typographyVariant = settingsManager.loadTypographyVariant(),
                    isDarkTheme = settingsManager.isDarkMode()
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading data")
            _state.value = MainState.Error(e.message ?: "Unknown Error")
        } finally {
            _isLoading.value = false
        }
    }

    private fun observeNetworkChanges() {
        coroutineScope.launch {
            networkManager.isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) {
                    Timber.d("Network connection restored. Refreshing data...")
                    refreshData()
                } else {
                    Timber.d("Network connection lost.")
                    _state.value = when (val currentState = _state.value) {
                        is MainState.DataLoaded -> currentState.copy(isNetworkAvailable = false)
                        else -> currentState
                    }
                }
            }
        }
    }

    private suspend fun updateFavoriteStatus(characterId: String, isFavorite: Boolean) {
        try {
            repository.updateFavoriteStatus(characterId, isFavorite)
            val updatedFavorites = _favoriteCharacters.value.toMutableMap()
            updatedFavorites[characterId] = isFavorite
            _favoriteCharacters.value = updatedFavorites
            _effect.emit(MainEffect.ShowToast(if (isFavorite) "Added to favorites" else "Removed from favorites"))
        } catch (e: Exception) {
            Timber.e(e, "Error updating favorite status")
            _effect.emit(MainEffect.ShowToast("Failed to update favorite status"))
        }
    }

    private suspend fun fetchCharacterDetails(characterId: String) {
        _isLoading.value = true
        try {
            val character = repository.fetchCharacterDetails(characterId)
            _selectedCharacter.value = character
            if (character != null) {
                _effect.emit(MainEffect.NavigateToDetails(character.id, "character"))
            } else {
                _effect.emit(MainEffect.ShowToast("Character not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching character details")
            _effect.emit(MainEffect.ShowToast("Failed to fetch character details"))
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun fetchStarshipDetails(starshipId: String) {
        _isLoading.value = true
        try {
            val starship = repository.fetchStarshipDetails(starshipId)
            _selectedStarship.value = starship
            starship?.let {
                _effect.emit(MainEffect.NavigateToDetails(it.id, "starship"))
            } ?: run {
                _effect.emit(MainEffect.ShowToast("Starship not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching starship details")
            _effect.emit(MainEffect.ShowToast("Failed to fetch starship details"))
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun fetchPlanetDetails(planetId: String) {
        _isLoading.value = true
        try {
            val planet = repository.fetchPlanetDetails(planetId)
            _selectedPlanet.value = planet
            planet?.let {
                _effect.emit(MainEffect.NavigateToDetails(it.id, "planet"))
            } ?: run {
                _effect.emit(MainEffect.ShowToast("Planet not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching planet details")
            _effect.emit(MainEffect.ShowToast("Failed to fetch planet details"))
        } finally {
            _isLoading.value = false
        }
    }

    internal suspend fun refreshData() {
        try {
            Timber.d("Refreshing data started")
            repository.refreshData()
            loadData()
            _effect.emit(MainEffect.ShowToast("Data refreshed"))
        } catch (e: Exception) {
            Timber.e(e, "Error refreshing data")
            _effect.emit(MainEffect.ShowToast("Failed to refresh data"))
        }
    }

    private suspend fun toggleTheme() {
        val isDarkMode = settingsManager.isDarkMode()
        settingsManager.setDarkMode(!isDarkMode)
        _state.value = when (val currentState = _state.value) {
            is MainState.DataLoaded -> currentState.copy(isDarkTheme = !isDarkMode)
            else -> _state.value
        }
        _effect.emit(MainEffect.ThemeChanged(!isDarkMode))
    }

    private fun toggleShowOnlyFavorites() {
        _state.value = when (val currentState = _state.value) {
            is MainState.DataLoaded -> currentState.copy(showOnlyFavorites = !currentState.showOnlyFavorites)
            else -> _state.value
        }
    }

    private fun createCharactersPager(): Flow<PagingData<StarWarsCharacter>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { CharacterPagingSource(this, _favoriteCharacters, repository, networkManager) }
        ).flow.cachedIn(coroutineScope)
    }

    private fun createStarshipsPager(): Flow<PagingData<Starship>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { StarshipPagingSource(this, repository, networkManager) }
        ).flow.cachedIn(coroutineScope)
    }

    private fun createPlanetsPager(): Flow<PagingData<Planet>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { PlanetPagingSource(this, repository, networkManager) }
        ).flow.cachedIn(coroutineScope)
    }

    suspend fun fetchStarships(after: String? = null, first: Int = PAGE_SIZE): StarshipsResponse {
        return repository.fetchStarships(after, first)
    }

    suspend fun fetchPlanets(after: String? = null, first: Int = PAGE_SIZE): PlanetsResponse {
        return repository.fetchPlanets(after, first)
    }

    suspend fun fetchCharacters(after: String? = null, first: Int = PAGE_SIZE): CharactersResponse {
        return repository.fetchCharacters(after, first)
    }

    suspend fun updateCharactersInDatabase(characters: List<StarWarsCharacter>) {
        repository.updateCharacters(characters)
    }

    suspend fun updateStarshipsInDatabase(starships: List<Starship>) {
        repository.updateStarships(starships)
    }

    suspend fun updatePlanetsInDatabase(planets: List<Planet>) {
        repository.updatePlanets(planets)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
