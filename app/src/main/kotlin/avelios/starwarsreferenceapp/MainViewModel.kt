package avelios.starwarsreferenceapp

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val apolloClient: ApolloClient,
    private val characterDao: CharacterDao,
    private val starshipDao: StarshipDao,
    private val planetDao: PlanetDao,
    internal val settingsManager: SettingsManager
) : ViewModel() {
    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state: StateFlow<MainState> = _state

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

    val charactersPager: Flow<PagingData<StarWarsCharacter>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { CharacterPagingSource(apolloClient) }
    ).flow.cachedIn(viewModelScope)

    val planetsPager: Flow<PagingData<Planet>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PlanetPagingSource(apolloClient) }
    ).flow.cachedIn(viewModelScope)

    val starshipsPager: Flow<PagingData<Starship>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { StarshipPagingSource(apolloClient) }
    ).flow.cachedIn(viewModelScope)

    init {
        handleIntent(MainIntent.LoadData)
    }

    fun handleIntent(intent: MainIntent) {
        viewModelScope.launch {
            when (intent) {
                is MainIntent.LoadData -> loadData()
                is MainIntent.UpdateFavoriteStatus -> updateFavoriteStatus(intent.characterId, intent.isFavorite)
                is MainIntent.FetchCharacterDetails -> fetchCharacterDetails(intent.characterId)
                is MainIntent.FetchStarshipDetails -> fetchStarshipDetails(intent.starshipId)
                is MainIntent.FetchPlanetDetails -> fetchPlanetDetails(intent.planetId)
            }
        }
    }

    private suspend fun loadData() {
        _state.value = MainState.Loading
        try {
            val charactersDeferred = viewModelScope.async(Dispatchers.IO) { fetchCharacters() }
            val starshipsDeferred = viewModelScope.async(Dispatchers.IO) { fetchStarships() }
            val planetsDeferred = viewModelScope.async(Dispatchers.IO) { fetchPlanets() }

            val characters = charactersDeferred.await()
            val starships = starshipsDeferred.await()
            val planets = planetsDeferred.await()

            _state.value = MainState.DataLoaded(characters, starships, planets)
        } catch (e: Exception) {
            _state.value = MainState.Error(e.message ?: "Unknown Error")
        }
    }

    private suspend fun updateFavoriteStatus(characterId: String, isFavorite: Boolean) {
        try {
            characterDao.updateFavoriteStatus(characterId, isFavorite)
            val currentState = _state.value
            if (currentState is MainState.DataLoaded) {
                val updatedCharacters = currentState.characters.map {
                    if (it.id == characterId) it.copy(isFavorite = isFavorite) else it
                }
                _state.value = currentState.copy(characters = updatedCharacters)
                Log.i("MainViewModel", "Character $characterId favorite status updated to $isFavorite")
            } else {
                Log.e("MainViewModel", "Failed to update favorite status: Current state is not DataLoaded")
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error updating favorite status: ${e.localizedMessage}")
        }
    }

    private suspend fun fetchCharacterDetails(characterId: String) {
        _isLoading.value = true
        try {
            val character = fetchCharacterDetailsFromServer(characterId)
            _selectedCharacter.value = character
        } catch (e: Exception) {
            _state.value = MainState.Error(e.message ?: "Unknown Error")
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun fetchCharacterDetailsFromServer(characterId: String): StarWarsCharacter? {
        val response = try {
            apolloClient.query(GetCharacterDetailsQuery(characterId)).execute()
        } catch (e: ApolloException) {
            println("ApolloException: $e")
            return null
        }

        return response.data?.person?.let { person ->
            StarWarsCharacter(
                id = person.id,
                name = person.name,
                birthYear = person.birthYear ?: "Unknown",
                eyeColor = person.eyeColor ?: "Unknown",
                gender = person.gender ?: "Unknown",
                hairColor = person.hairColor ?: "Unknown",
                height = person.height ?: 0,
                mass = person.mass ?: 0.0,
                homeworld = person.homeworld?.name ?: "Unknown",
                filmsCount = person.filmConnection?.totalCount ?: 0,
                skinColor = person.skinColor ?: "Unknown"
            )
        }
    }

    private suspend fun fetchStarshipDetails(starshipId: String) {
        _isLoading.value = true
        try {
            val starship = fetchStarshipDetailsFromServer(starshipId)
            _selectedStarship.value = starship
        } catch (e: Exception) {
            _state.value = MainState.Error(e.message ?: "Unknown Error")
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun fetchStarshipDetailsFromServer(starshipId: String): Starship? {
        val response = try {
            apolloClient.query(GetStarshipDetailsQuery(starshipId)).execute()
        } catch (e: ApolloException) {
            println("ApolloException: $e")
            return null
        }

        return response.data?.starship?.let { starship ->
            Starship(
                id = starship.id,
                name = starship.name,
                model = starship.model ?: "",
                starshipClass = starship.starshipClass ?: "",
                manufacturers = starship.manufacturers?.filterNotNull() ?: emptyList(),
                length = starship.length?.toFloat() ?: 0f,
                crew = starship.crew ?: "",
                passengers = starship.passengers ?: "",
                maxAtmospheringSpeed = starship.maxAtmospheringSpeed ?: 0,
                hyperdriveRating = starship.hyperdriveRating?.toFloat() ?: 0f
            )
        }
    }

    private suspend fun fetchPlanetDetailsFromServer(planetId: String): Planet? {
        val response = try {
            apolloClient.query(GetPlanetDetailsQuery(planetId)).execute()
        } catch (e: ApolloException) {
            println("ApolloException: $e")
            return null
        }

        return response.data?.planet?.let { planet ->
            Planet(
                id = planet.id,
                name = planet.name,
                climates = planet.climates?.mapNotNull { it } ?: emptyList(),
                diameter = planet.diameter ?: 0,
                rotationPeriod = planet.rotationPeriod ?: 0,
                orbitalPeriod = planet.orbitalPeriod ?: 0,
                gravity = planet.gravity ?: "Unknown",
                population = planet.population ?: 0.0,
                terrains = planet.terrains?.mapNotNull { it } ?: emptyList(),
                surfaceWater = planet.surfaceWater ?: 0.0
            )
        }
    }

    private suspend fun fetchPlanetDetails(planetId: String) {
        _isLoading.value = true
        try {
            val planet = fetchPlanetDetailsFromServer(planetId)
            _selectedPlanet.value = planet
        } catch (e: Exception) {
            _state.value = MainState.Error(e.message ?: "Unknown Error")
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun fetchCharacters(): List<StarWarsCharacter> {
        val cachedCharacters = characterDao.getAllCharacters()
        if (cachedCharacters.isNotEmpty()) {
            return cachedCharacters
        }

        val charactersList = getCharactersFromServer() ?: emptyList()
        if (charactersList.isNotEmpty()) {
            characterDao.insertCharacters(*charactersList.toTypedArray())
        }
        return charactersList
    }

    private suspend fun fetchStarships(): List<Starship> {
        val cachedStarships = starshipDao.getAllStarships()
        if (cachedStarships.isNotEmpty()) {
            return cachedStarships
        }

        val starshipsList = getStarshipsFromServer() ?: emptyList()
        if (starshipsList.isNotEmpty()) {
            starshipDao.insertStarships(*starshipsList.toTypedArray())
        }
        return starshipsList
    }

    private suspend fun fetchPlanets(): List<Planet> {
        val cachedPlanets = planetDao.getAllPlanets()
        if (cachedPlanets.isNotEmpty()) {
            return cachedPlanets
        }

        val planetsList = getPlanetsFromServer() ?: emptyList()
        if (planetsList.isNotEmpty()) {
            planetDao.insertPlanets(*planetsList.toTypedArray())
        }
        return planetsList
    }

    private suspend fun getCharactersFromServer(after: String? = null, first: Int = 10): List<StarWarsCharacter>? {
        val response = try {
            apolloClient.query(GetCharactersQuery(Optional.presentIfNotNull(after), Optional.present(first))).execute()
        } catch (e: ApolloException) {
            println("ApolloException: $e")
            return null
        }

        return response.data?.allPeople?.edges?.mapNotNull { edge ->
            edge?.node?.let { person: GetCharactersQuery.Node ->
                StarWarsCharacter(
                    id = person.id,
                    name = person.name,
                    filmsCount = person.filmConnection?.totalCount ?: 0,
                    birthYear = person.birthYear ?: "Unknown",
                    eyeColor = person.eyeColor ?: "Unknown",
                    gender = person.gender ?: "Unknown",
                    hairColor = person.hairColor ?: "Unknown",
                    height = person.height ?: 0,
                    mass = person.mass ?: 0.0,
                    skinColor = person.skinColor ?: "Unknown",
                    homeworld = person.homeworld?.name ?: "Unknown"
                )
            }
        } ?: emptyList()
    }

    private suspend fun getStarshipsFromServer(after: String? = null, first: Int = 10): List<Starship>? {
        val response = try {
            apolloClient.query(GetStarshipsQuery(Optional.presentIfNotNull(after), Optional.present(first))).execute()
        } catch (e: ApolloException) {
            println("ApolloException: $e")
            return null
        }

        return response.data?.allStarships?.edges?.mapNotNull { edge ->
            edge?.node?.let { starship ->
                Starship(
                    id = starship.id,
                    name = starship.name,
                    model = starship.model ?: "",
                    starshipClass = starship.starshipClass ?: "",
                    manufacturers = starship.manufacturers?.filterNotNull() ?: emptyList(),
                    length = starship.length?.toFloat() ?: 0f,
                    crew = starship.crew ?: "",
                    passengers = starship.passengers ?: "",
                    maxAtmospheringSpeed = starship.maxAtmospheringSpeed ?: 0,
                    hyperdriveRating = starship.hyperdriveRating?.toFloat() ?: 0f
                )
            }
        } ?: emptyList()
    }

    private suspend fun getPlanetsFromServer(after: String? = null, first: Int = 10): List<Planet>? {
        val response = try {
            apolloClient.query(GetPlanetsQuery(Optional.presentIfNotNull(after), Optional.present(first))).execute()
        } catch (e: ApolloException) {
            println("ApolloException: $e")
            return null
        }

        return response.data?.allPlanets?.edges?.mapNotNull { edge ->
            edge?.node?.let { planet ->
                Planet(
                    id = planet.id,
                    name = planet.name,
                    climates = planet.climates?.mapNotNull { it } ?: emptyList(),
                    diameter = planet.diameter ?: 0,
                    rotationPeriod = planet.rotationPeriod ?: 0,
                    orbitalPeriod = planet.orbitalPeriod ?: 0,
                    gravity = planet.gravity ?: "Unknown",
                    population = planet.population ?: 0.0,
                    terrains = planet.terrains?.mapNotNull { it } ?: emptyList(),
                    surfaceWater = planet.surfaceWater ?: 0.0
                )
            }
        } ?: emptyList()
    }

    fun areListsEmpty(): Boolean {
        val currentState = _state.value
        return if (currentState is MainState.DataLoaded) {
            currentState.characters.isEmpty() || currentState.starships.isEmpty() || currentState.planets.isEmpty()
        } else {
            true
        }
    }

    fun refreshData() {
        viewModelScope.launch { loadData() }
    }

    fun toggleTheme() {
        val newTheme = !_isDarkTheme.value
        _isDarkTheme.value = newTheme
        settingsManager.setDarkMode(newTheme)
        AppCompatDelegate.setDefaultNightMode(
            if (newTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
