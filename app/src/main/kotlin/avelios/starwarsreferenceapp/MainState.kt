package avelios.starwarsreferenceapp

import timber.log.Timber

internal sealed class MainState {
    data object Loading : MainState()
    data class DataLoaded(
        val characters: List<StarWarsCharacter> = emptyList(),
        val starships: List<Starship> = emptyList(),
        val planets: List<Planet> = emptyList()
    ) : MainState()

    data class Error(val message: String) : MainState()
}

internal sealed class MainIntent {
    data object LoadData : MainIntent()
    data class UpdateFavoriteStatus(val characterId: String, val isFavorite: Boolean) : MainIntent()
    data class FetchCharacterDetails(val characterId: String) : MainIntent()
    data class FetchStarshipDetails(val starshipId: String) : MainIntent()
    data class FetchPlanetDetails(val planetId: String) : MainIntent()
    data object RefreshData : MainIntent()
}

internal sealed class MainEffect {
    data class ShowToast(val message: String) : MainEffect()
    data class NavigateToDetails(val id: String, val type: String) : MainEffect()
    data class ThemeChanged(val isDarkTheme: Boolean) : MainEffect()
}

internal sealed class MainNews {
    data object DataLoaded : MainNews()
    data object DataRefreshed : MainNews()
    data class ErrorOccurred(val message: String) : MainNews()
}

data class CharactersResponse(
    val characters: List<StarWarsCharacter>,
    val pageInfo: PageInfo
)

data class PageInfo(
    val endCursor: String?,
    val hasNextPage: Boolean
)

internal class MainActor(
    private val repository: StarWarsRepository
) {
    suspend fun loadData(): MainState {
        return try {
            val characters = repository.getAllCharacters()
            val starships = repository.fetchStarships()
            val planets = repository.fetchPlanets()
            MainState.DataLoaded(characters, starships, planets)
        } catch (e: Exception) {
            Timber.e(e, "Error loading data")
            MainState.Error(e.message ?: "Unknown Error")
        }
    }

    suspend fun updateFavoriteStatus(characterId: String, isFavorite: Boolean) {
        try {
            repository.updateFavoriteStatus(characterId, isFavorite)
        } catch (e: Exception) {
            Timber.e(e, "Error updating favorite status for characterId: $characterId")
        }
    }

    suspend fun fetchCharacterDetails(characterId: String): StarWarsCharacter? {
        return repository.fetchCharacterDetails(characterId)
    }

    suspend fun loadFavoriteCharacters(): Map<String, Boolean> {
        return repository.loadFavoriteCharacters()
    }

    suspend fun fetchStarshipDetails(starshipId: String): Starship? {
        return repository.fetchStarshipDetails(starshipId)
    }

    suspend fun fetchPlanetDetails(planetId: String): Planet? {
        return repository.fetchPlanetDetails(planetId)
    }

    suspend fun fetchCharacters(after: String? = null, first: Int = 10): CharactersResponse {
        return repository.fetchCharacters(after, first)
    }

    suspend fun fetchStarships(after: String? = null, first: Int = 10): List<Starship> {
        return repository.fetchStarships(after, first)
    }

    suspend fun fetchPlanets(after: String? = null, first: Int = 10): List<Planet> {
        return repository.fetchPlanets(after, first)
    }

    suspend fun refreshData() {
        repository.refreshData()
    }
}
