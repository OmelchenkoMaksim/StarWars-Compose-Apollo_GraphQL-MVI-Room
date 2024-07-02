package avelios.starwarsreferenceapp

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import timber.log.Timber

class StarWarsRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val characterDao: CharacterDao,
    private val starshipDao: StarshipDao,
    private val planetDao: PlanetDao
) : StarWarsRepository {
    override suspend fun getAllCharacters(): List<StarWarsCharacter> {
        return characterDao.getAllCharacters()
    }

    override suspend fun getAllStarships(): List<Starship> {
        return starshipDao.getAllStarships()
    }

    override suspend fun getAllPlanets(): List<Planet> {
        return planetDao.getAllPlanets()
    }

    override suspend fun updateFavoriteStatus(characterId: String, isFavorite: Boolean) {
        characterDao.updateFavoriteStatus(characterId, isFavorite)
    }

    override suspend fun updateCharacters(characters: List<StarWarsCharacter>) {
        characterDao.insertCharacters(*characters.toTypedArray())
    }

    override suspend fun updateStarships(starships: List<Starship>) {
        starshipDao.insertStarships(starships)
    }

    override suspend fun updatePlanets(planets: List<Planet>) {
        planetDao.insertPlanets(planets)
    }

    override suspend fun fetchCharacterDetails(characterId: String): StarWarsCharacter? {
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
            Timber.e(e, "Error fetching character details for characterId: $characterId")
            null
        }
    }

    override suspend fun fetchStarshipDetails(starshipId: String): Starship? {
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
            Timber.e(e, "Error fetching starship details for starshipId: $starshipId")
            null
        }
    }

    override suspend fun fetchPlanetDetails(planetId: String): Planet? {
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
            Timber.e(e, "Error fetching planet details for planetId: $planetId")
            null
        }
    }

    override suspend fun fetchCharacters(after: String?, first: Int): CharactersResponse {
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
            Timber.e(e, "Error fetching characters")
            CharactersResponse(emptyList(), PageInfo(null, false))
        }
    }

    override suspend fun fetchStarships(after: String?, first: Int): StarshipsResponse {
        return try {
            val response: ApolloResponse<GetStarshipsQuery.Data> =
                apolloClient.query(
                    GetStarshipsQuery(
                        Optional.presentIfNotNull(after),
                        Optional.present(first)
                    )
                ).execute()

            val starships = response.data?.allStarships?.edges?.mapNotNull { edge ->
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

            val pageInfo = PageInfo(
                endCursor = response.data?.allStarships?.pageInfo?.endCursor,
                hasNextPage = response.data?.allStarships?.pageInfo?.hasNextPage ?: false
            )

            StarshipsResponse(starships, pageInfo)
        } catch (e: ApolloException) {
            Timber.e(e, "Error fetching starships")
            StarshipsResponse(emptyList(), PageInfo(null, false))
        }
    }

    override suspend fun fetchPlanets(after: String?, first: Int): PlanetsResponse {
        return try {
            val response: ApolloResponse<GetPlanetsQuery.Data> =
                apolloClient.query(
                    GetPlanetsQuery(
                        Optional.presentIfNotNull(after),
                        Optional.present(first)
                    )
                ).execute()

            val planets = response.data?.allPlanets?.edges?.mapNotNull { edge ->
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

            val pageInfo = PageInfo(
                endCursor = response.data?.allPlanets?.pageInfo?.endCursor,
                hasNextPage = response.data?.allPlanets?.pageInfo?.hasNextPage ?: false
            )

            PlanetsResponse(planets, pageInfo)
        } catch (e: ApolloException) {
            Timber.e(e, "Error fetching planets")
            PlanetsResponse(emptyList(), PageInfo(null, false))
        }
    }

    override suspend fun refreshData() {
        try {
            val charactersResponse: CharactersResponse = fetchCharacters()
            val starshipsResponse: StarshipsResponse = fetchStarships()
            val planetsResponse: PlanetsResponse = fetchPlanets()

            characterDao.insertCharacters(*charactersResponse.characters.toTypedArray())
            starshipDao.insertStarships(*starshipsResponse.starships.toTypedArray())
            planetDao.insertPlanets(*planetsResponse.planets.toTypedArray())
        } catch (e: Exception) {
            Timber.e(e, "Error refreshing data: ${e.message}")
        }
    }

    internal companion object {
        const val UNKNOWN = "Unknown"
        const val EMPTY_STRING = ""
        const val ZERO_FLOAT = 0f
        const val ZERO_DOUBLE = 0.0
    }
}

interface StarWarsRepository {
    suspend fun getAllCharacters(): List<StarWarsCharacter>
    suspend fun getAllStarships(): List<Starship>
    suspend fun getAllPlanets(): List<Planet>
    suspend fun updateFavoriteStatus(characterId: String, isFavorite: Boolean)
    suspend fun updateCharacters(characters: List<StarWarsCharacter>)
    suspend fun updateStarships(starships: List<Starship>)
    suspend fun updatePlanets(planets: List<Planet>)
    suspend fun fetchCharacterDetails(characterId: String): StarWarsCharacter?
    suspend fun fetchStarshipDetails(starshipId: String): Starship?
    suspend fun fetchPlanetDetails(planetId: String): Planet?
    suspend fun fetchCharacters(after: String? = null, first: Int = PAGE_SIZE): CharactersResponse
    suspend fun fetchStarships(after: String? = null, first: Int = PAGE_SIZE): StarshipsResponse
    suspend fun fetchPlanets(after: String? = null, first: Int = PAGE_SIZE): PlanetsResponse
    suspend fun refreshData()
}
