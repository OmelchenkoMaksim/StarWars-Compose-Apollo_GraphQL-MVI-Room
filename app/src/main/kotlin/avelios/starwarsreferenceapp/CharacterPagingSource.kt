package avelios.starwarsreferenceapp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

internal class CharacterPagingSource(
    private val actor: MainActor,
    private val favoriteCharactersFlow: StateFlow<Map<String, Boolean>>,
    private val repository: StarWarsRepository,
    private val networkManager: NetworkManager
) : PagingSource<String, StarWarsCharacter>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, StarWarsCharacter> {
        return try {
            val isNetworkAvailable = networkManager.isNetworkAvailable.value
            val favoriteCharacters = favoriteCharactersFlow.value

            if (isNetworkAvailable) {
                val response = actor.fetchCharacters(params.key, params.loadSize)
                val updatedCharacters = response.characters.map { character ->
                    character.copy(isFavorite = favoriteCharacters[character.id] ?: character.isFavorite)
                }
                actor.updateCharactersInDatabase(updatedCharacters)
                LoadResult.Page(
                    data = updatedCharacters,
                    prevKey = null,
                    nextKey = response.pageInfo.endCursor.takeIf { response.pageInfo.hasNextPage }
                )
            } else {
                val localCharacters = repository.getAllCharacters()
                val updatedCharacters = localCharacters.map { character ->
                    character.copy(isFavorite = favoriteCharacters[character.id] ?: character.isFavorite)
                }
                LoadResult.Page(
                    data = updatedCharacters,
                    prevKey = null,
                    nextKey = null // No pagination for local data
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading characters")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, StarWarsCharacter>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}

internal class StarshipPagingSource(
    private val actor: MainActor,
    private val repository: StarWarsRepository,
    private val networkManager: NetworkManager
) : PagingSource<String, Starship>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Starship> {
        return try {
            val isNetworkAvailable = networkManager.isNetworkAvailable.value

            if (isNetworkAvailable) {
                val response = actor.fetchStarships(params.key, params.loadSize)
                actor.updateStarshipsInDatabase(response.starships)
                LoadResult.Page(
                    data = response.starships.distinctBy { it.id },
                    prevKey = null,
                    nextKey = response.pageInfo.endCursor.takeIf { response.pageInfo.hasNextPage }
                )
            } else {
                val localStarships = repository.getAllStarships()
                LoadResult.Page(
                    data = localStarships.distinctBy { it.id },
                    prevKey = null,
                    nextKey = null // No pagination for local data
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading starships")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Starship>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}

internal class PlanetPagingSource(
    private val actor: MainActor,
    private val repository: StarWarsRepository,
    private val networkManager: NetworkManager
) : PagingSource<String, Planet>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Planet> {
        return try {
            val isNetworkAvailable = networkManager.isNetworkAvailable.value

            if (isNetworkAvailable) {
                val response = actor.fetchPlanets(params.key, params.loadSize)
                actor.updatePlanetsInDatabase(response.planets)
                LoadResult.Page(
                    data = response.planets.distinctBy { it.id },
                    prevKey = null,
                    nextKey = response.pageInfo.endCursor.takeIf { response.pageInfo.hasNextPage }
                )
            } else {
                val localPlanets = repository.getAllPlanets()
                LoadResult.Page(
                    data = localPlanets.distinctBy { it.id },
                    prevKey = null,
                    nextKey = null // No pagination for local data
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading planets")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Planet>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}
