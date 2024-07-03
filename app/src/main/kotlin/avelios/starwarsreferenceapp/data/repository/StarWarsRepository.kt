package avelios.starwarsreferenceapp.data.repository

import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship
import avelios.starwarsreferenceapp.domain.model.CharactersResponse
import avelios.starwarsreferenceapp.domain.model.PlanetsResponse
import avelios.starwarsreferenceapp.domain.model.StarshipsResponse

/**
 * Interface defining the contract for interacting with the Star Wars data sources.
 */
interface StarWarsRepository {
    /**
     * Retrieves all Star Wars characters from the local database.
     *
     * @return A list of all characters.
     */
    suspend fun getAllCharacters(): List<StarWarsCharacter>

    /**
     * Retrieves all Star Wars starships from the local database.
     *
     * @return A list of all starships.
     */
    suspend fun getAllStarships(): List<Starship>

    /**
     * Retrieves all Star Wars planets from the local database.
     *
     * @return A list of all planets.
     */
    suspend fun getAllPlanets(): List<Planet>

    /**
     * Updates the favorite status of a character.
     *
     * @param characterId The ID of the character.
     * @param isFavorite The new favorite status of the character.
     */
    suspend fun updateFavoriteStatus(characterId: String, isFavorite: Boolean)

    /**
     * Updates the list of characters in the local database.
     *
     * @param characters The list of characters to update.
     */
    suspend fun updateCharacters(characters: List<StarWarsCharacter>)

    /**
     * Updates the list of starships in the local database.
     *
     * @param starships The list of starships to update.
     */
    suspend fun updateStarships(starships: List<Starship>)

    /**
     * Updates the list of planets in the local database.
     *
     * @param planets The list of planets to update.
     */
    suspend fun updatePlanets(planets: List<Planet>)

    /**
     * Fetches the details of a specific character by ID.
     *
     * @param characterId The ID of the character to fetch details for.
     * @return The character details or null if not found.
     */
    suspend fun fetchCharacterDetails(characterId: String): StarWarsCharacter?

    /**
     * Fetches the details of a specific starship by ID.
     *
     * @param starshipId The ID of the starship to fetch details for.
     * @return The starship details or null if not found.
     */
    suspend fun fetchStarshipDetails(starshipId: String): Starship?

    /**
     * Fetches the details of a specific planet by ID.
     *
     * @param planetId The ID of the planet to fetch details for.
     * @return The planet details or null if not found.
     */
    suspend fun fetchPlanetDetails(planetId: String): Planet?

    /**
     * Fetches a list of characters using pagination.
     *
     * @param after The cursor indicating the position after which to fetch the characters.
     * @param first The number of characters to fetch.
     * @return A CharactersResponse containing the fetched characters and pagination info.
     */
    suspend fun fetchCharacters(after: String?, first: Int): CharactersResponse

    /**
     * Fetches a list of starships using pagination.
     *
     * @param after The cursor indicating the position after which to fetch the starships.
     * @param first The number of starships to fetch.
     * @return A StarshipsResponse containing the fetched starships and pagination info.
     */
    suspend fun fetchStarships(after: String?, first: Int): StarshipsResponse

    /**
     * Fetches a list of planets using pagination.
     *
     * @param after The cursor indicating the position after which to fetch the planets.
     * @param first The number of planets to fetch.
     * @return A PlanetsResponse containing the fetched planets and pagination info.
     */
    suspend fun fetchPlanets(after: String?, first: Int): PlanetsResponse

    /**
     * Refreshes the data in the local database by fetching the latest data from the network.
     */
    suspend fun refreshData()
}
