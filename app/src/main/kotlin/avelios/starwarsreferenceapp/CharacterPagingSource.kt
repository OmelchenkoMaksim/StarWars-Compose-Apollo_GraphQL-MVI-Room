package avelios.starwarsreferenceapp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

internal class CharacterPagingSource(
    private val actor: MainActor,
    private val favoriteCharactersFlow: StateFlow<Map<String, Boolean>>
) : PagingSource<String, StarWarsCharacter>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, StarWarsCharacter> {
        return try {
            val response = actor.fetchCharacters(params.key, params.loadSize)
            val favoriteCharacters = favoriteCharactersFlow.value

            val updatedCharacters = response.characters.map { character ->
                character.copy(isFavorite = favoriteCharacters[character.id] ?: character.isFavorite)
            }

            LoadResult.Page(
                data = updatedCharacters,
                prevKey = null,
                nextKey = response.pageInfo.endCursor.takeIf { response.pageInfo.hasNextPage }
            )
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

internal class PlanetPagingSource(
    private val actor: MainActor
) : PagingSource<String, Planet>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Planet> {
        return try {
            val planets = actor.fetchPlanets(params.key, params.loadSize)

            LoadResult.Page(
                data = planets,
                prevKey = null,
                nextKey = planets.lastOrNull()?.id
            )
        } catch (e: Exception) {
            Timber.e(e, "Error loading planets")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Planet>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}

internal class StarshipPagingSource(
    private val actor: MainActor
) : PagingSource<String, Starship>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Starship> {
        return try {
            val starships = actor.fetchStarships(params.key, params.loadSize)

            LoadResult.Page(
                data = starships,
                prevKey = null,
                nextKey = starships.lastOrNull()?.id
            )
        } catch (e: Exception) {
            Timber.e(e, "Error loading starships")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Starship>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}
