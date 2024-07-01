package avelios.starwarsreferenceapp

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException

sealed class MainState {
    data object Loading : MainState()
    data class DataLoaded(
        val characters: List<StarWarsCharacter> = emptyList(),
        val starships: List<Starship> = emptyList(),
        val planets: List<Planet> = emptyList()
    ) : MainState()

    data class Error(val message: String) : MainState()
}

sealed class MainIntent {
    data object LoadData : MainIntent()
    data class UpdateFavoriteStatus(val characterId: String, val isFavorite: Boolean) : MainIntent()
    data class FetchCharacterDetails(val characterId: String) : MainIntent()
    data class FetchStarshipDetails(val starshipId: String) : MainIntent()
    data class FetchPlanetDetails(val planetId: String) : MainIntent()
    data object RefreshData : MainIntent()
}

sealed class MainEffect {
    data class ShowToast(val message: String) : MainEffect()
    data class NavigateToDetails(val id: String, val type: String) : MainEffect()
    data class ThemeChanged(val isDarkTheme: Boolean) : MainEffect()
}

sealed class MainNews {
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

class MainActor(
    private val apolloClient: ApolloClient,
    private val characterDao: CharacterDao,
    private val starshipDao: StarshipDao,
    private val planetDao: PlanetDao
) {
    suspend fun loadData(): MainState {
        return try {
            val characters: List<StarWarsCharacter> = characterDao.getAllCharacters()
            val starships: List<Starship> = starshipDao.getAllStarships()
            val planets: List<Planet> = planetDao.getAllPlanets()
            MainState.DataLoaded(characters, starships, planets)
        } catch (e: Exception) {
            MainState.Error(e.message ?: "Unknown Error")
        }
    }

    suspend fun updateFavoriteStatus(characterId: String, isFavorite: Boolean) {
        characterDao.updateFavoriteStatus(characterId, isFavorite)
    }

    suspend fun fetchCharacterDetails(characterId: String): StarWarsCharacter? {
        return try {
            val response = apolloClient.query(GetCharacterDetailsQuery(characterId)).execute()
            response.data?.person?.let { person ->
                StarWarsCharacter(
                    id = person.id,
                    name = person.name,
                    birthYear = person.birthYear ?: UNKNOWN,
                    eyeColor = person.eyeColor ?: UNKNOWN,
                    gender = person.gender ?: UNKNOWN,
                    hairColor = person.hairColor ?: UNKNOWN,
                    height = person.height ?: 0,
                    mass = person.mass ?: ZERO_DOUBLE,
                    homeworld = person.homeworld?.name ?: UNKNOWN,
                    filmsCount = person.filmConnection?.totalCount ?: 0,
                    skinColor = person.skinColor ?: UNKNOWN,
                    isFavorite = characterDao.getFavoriteStatus(person.id) ?: false
                )
            }
        } catch (e: ApolloException) {
            null
        }
    }

    suspend fun loadFavoriteCharacters(): Map<String, Boolean> {
        return try {
            val characters: List<StarWarsCharacter> = characterDao.getAllCharacters()
            characters.associate { it.id to it.isFavorite }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    suspend fun fetchStarshipDetails(starshipId: String): Starship? {
        return try {
            val response = apolloClient.query(GetStarshipDetailsQuery(starshipId)).execute()
            response.data?.starship?.let { starship ->
                Starship(
                    id = starship.id,
                    name = starship.name,
                    model = starship.model ?: EMPTY_STRING,
                    starshipClass = starship.starshipClass ?: EMPTY_STRING,
                    manufacturers = starship.manufacturers?.filterNotNull() ?: emptyList(),
                    length = starship.length?.toFloat() ?: ZERO_FLOAT,
                    crew = starship.crew ?: EMPTY_STRING,
                    passengers = starship.passengers ?: EMPTY_STRING,
                    maxAtmospheringSpeed = starship.maxAtmospheringSpeed ?: 0,
                    hyperdriveRating = starship.hyperdriveRating?.toFloat() ?: ZERO_FLOAT
                )
            }
        } catch (e: ApolloException) {
            null
        }
    }

    suspend fun fetchPlanetDetails(planetId: String): Planet? {
        return try {
            val response = apolloClient.query(GetPlanetDetailsQuery(planetId)).execute()
            response.data?.planet?.let { planet ->
                Planet(
                    id = planet.id,
                    name = planet.name,
                    climates = planet.climates?.mapNotNull { it } ?: emptyList(),
                    diameter = planet.diameter ?: 0,
                    rotationPeriod = planet.rotationPeriod ?: 0,
                    orbitalPeriod = planet.orbitalPeriod ?: 0,
                    gravity = planet.gravity ?: UNKNOWN,
                    population = planet.population ?: ZERO_DOUBLE,
                    terrains = planet.terrains?.mapNotNull { it } ?: emptyList(),
                    surfaceWater = planet.surfaceWater ?: ZERO_DOUBLE
                )
            }
        } catch (e: ApolloException) {
            null
        }
    }

    suspend fun fetchCharacters(after: String? = null, first: Int = 10): CharactersResponse {
        return try {
            val response: ApolloResponse<GetCharactersQuery.Data> =
                apolloClient.query(
                    GetCharactersQuery(
                        Optional.presentIfNotNull(after),
                        Optional.present(first)
                    )
                ).execute()
            val characters = response.data?.allPeople?.edges?.mapNotNull { edge ->
                edge?.node?.let { person ->
                    StarWarsCharacter(
                        id = person.id,
                        name = person.name,
                        filmsCount = person.filmConnection?.totalCount ?: 0,
                        birthYear = person.birthYear ?: UNKNOWN,
                        eyeColor = person.eyeColor ?: UNKNOWN,
                        gender = person.gender ?: UNKNOWN,
                        hairColor = person.hairColor ?: UNKNOWN,
                        height = person.height ?: 0,
                        mass = person.mass ?: ZERO_DOUBLE,
                        skinColor = person.skinColor ?: UNKNOWN,
                        homeworld = person.homeworld?.name ?: UNKNOWN,
                        isFavorite = characterDao.getFavoriteStatus(person.id) ?: false
                    )
                }
            } ?: emptyList()

            val pageInfo = PageInfo(
                endCursor = response.data?.allPeople?.pageInfo?.endCursor,
                hasNextPage = response.data?.allPeople?.pageInfo?.hasNextPage ?: false
            )

            CharactersResponse(characters, pageInfo)
        } catch (e: ApolloException) {
            CharactersResponse(emptyList(), PageInfo(null, false))
        }
    }

    suspend fun fetchStarships(after: String? = null, first: Int = 10): List<Starship> {
        return try {
            val response: ApolloResponse<GetStarshipsQuery.Data> =
                apolloClient.query(
                    GetStarshipsQuery(
                        Optional.presentIfNotNull(after),
                        Optional.present(first)
                    )
                ).execute()

            response.data?.allStarships?.edges?.mapNotNull { edge ->
                edge?.node?.let { starship ->
                    Starship(
                        id = starship.id,
                        name = starship.name,
                        model = starship.model ?: EMPTY_STRING,
                        starshipClass = starship.starshipClass ?: EMPTY_STRING,
                        manufacturers = starship.manufacturers?.filterNotNull() ?: emptyList(),
                        length = starship.length?.toFloat() ?: ZERO_FLOAT,
                        crew = starship.crew ?: EMPTY_STRING,
                        passengers = starship.passengers ?: EMPTY_STRING,
                        maxAtmospheringSpeed = starship.maxAtmospheringSpeed ?: 0,
                        hyperdriveRating = starship.hyperdriveRating?.toFloat() ?: ZERO_FLOAT
                    )
                }
            } ?: emptyList()
        } catch (e: ApolloException) {
            emptyList()
        }
    }

    suspend fun fetchPlanets(after: String? = null, first: Int = 10): List<Planet> {
        return try {
            val response: ApolloResponse<GetPlanetsQuery.Data> =
                apolloClient.query(
                    GetPlanetsQuery(
                        Optional.presentIfNotNull(after),
                        Optional.present(first)
                    )
                ).execute()
            response.data?.allPlanets?.edges?.mapNotNull { edge ->
                edge?.node?.let { planet ->
                    Planet(
                        id = planet.id,
                        name = planet.name,
                        climates = planet.climates?.mapNotNull { it } ?: emptyList(),
                        diameter = planet.diameter ?: 0,
                        rotationPeriod = planet.rotationPeriod ?: 0,
                        orbitalPeriod = planet.orbitalPeriod ?: 0,
                        gravity = planet.gravity ?: UNKNOWN,
                        population = planet.population ?: ZERO_DOUBLE,
                        terrains = planet.terrains?.mapNotNull { it } ?: emptyList(),
                        surfaceWater = planet.surfaceWater ?: ZERO_DOUBLE
                    )
                }
            } ?: emptyList()
        } catch (e: ApolloException) {
            emptyList()
        }
    }

    suspend fun refreshData() {
        val charactersResponse: CharactersResponse = fetchCharacters()
        val starships: List<Starship> = fetchStarships()
        val planets: List<Planet> = fetchPlanets()

        characterDao.insertCharacters(*charactersResponse.characters.toTypedArray())
        starshipDao.insertStarships(*starships.toTypedArray())
        planetDao.insertPlanets(*planets.toTypedArray())
    }

    internal companion object {
        const val UNKNOWN = "Unknown"
        const val EMPTY_STRING = ""
        const val ZERO_FLOAT = 0f
        const val ZERO_DOUBLE = 0.0
    }
}
