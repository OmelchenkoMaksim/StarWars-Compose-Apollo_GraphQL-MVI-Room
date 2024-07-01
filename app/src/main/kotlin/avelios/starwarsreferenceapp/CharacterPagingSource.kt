package avelios.starwarsreferenceapp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional

class CharacterPagingSource(
    private val apolloClient: ApolloClient
) : PagingSource<String, StarWarsCharacter>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, StarWarsCharacter> {
        return try {
            val response = apolloClient.query(GetCharactersQuery(Optional.presentIfNotNull(params.key), Optional.present(params.loadSize))).execute()
            val characters = response.data?.allPeople?.edges?.mapNotNull { edge ->
                edge?.node?.let { person ->
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

            val nextKey = response.data?.allPeople?.pageInfo?.endCursor

            LoadResult.Page(
                data = characters,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, StarWarsCharacter>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}

class PlanetPagingSource(
    private val apolloClient: ApolloClient
) : PagingSource<String, Planet>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Planet> {
        return try {
            val response = apolloClient.query(GetPlanetsQuery(Optional.presentIfNotNull(params.key), Optional.present(params.loadSize))).execute()
            val planets = response.data?.allPlanets?.edges?.mapNotNull { edge ->
                edge?.node?.let { planet ->
                    Planet(
                        id = planet.id,
                        name = planet.name,
                        climates = planet.climates?.filterNotNull() ?: emptyList(),
                        diameter = planet.diameter ?: 0,
                        rotationPeriod = planet.rotationPeriod ?: 0,
                        orbitalPeriod = planet.orbitalPeriod ?: 0,
                        gravity = planet.gravity ?: "Unknown",
                        population = planet.population ?: 0.0,
                        terrains = planet.terrains?.filterNotNull() ?: emptyList(),
                        surfaceWater = planet.surfaceWater ?: 0.0
                    )
                }
            } ?: emptyList()

            val nextKey = response.data?.allPlanets?.pageInfo?.endCursor

            LoadResult.Page(
                data = planets,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Planet>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}

class StarshipPagingSource(
    private val apolloClient: ApolloClient
) : PagingSource<String, Starship>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Starship> {
        return try {
            val response = apolloClient.query(GetStarshipsQuery(Optional.presentIfNotNull(params.key), Optional.present(params.loadSize))).execute()
            val starships = response.data?.allStarships?.edges?.mapNotNull { edge ->
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

            val nextKey = response.data?.allStarships?.pageInfo?.endCursor

            LoadResult.Page(
                data = starships,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Starship>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }
}
