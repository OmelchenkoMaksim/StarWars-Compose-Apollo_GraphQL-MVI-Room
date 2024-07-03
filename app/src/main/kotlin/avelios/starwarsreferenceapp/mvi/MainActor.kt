package avelios.starwarsreferenceapp.mvi

import androidx.paging.PagingData
import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship
import avelios.starwarsreferenceapp.domain.model.CharactersResponse
import avelios.starwarsreferenceapp.domain.model.PlanetsResponse
import avelios.starwarsreferenceapp.domain.model.StarshipsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface defining the contract for handling actions and managing state within the app.
 *
 * @property state The current state of the application as a StateFlow.
 * @property favoriteCharacters A StateFlow containing the favorite status of characters.
 * @property charactersPager A StateFlow containing a Flow of PagingData for characters.
 * @property starshipsPager A StateFlow containing a Flow of PagingData for starships.
 * @property planetsPager A StateFlow containing a Flow of PagingData for planets.
 * @property selectedCharacter A StateFlow containing the selected character.
 * @property selectedStarship A StateFlow containing the selected starship.
 * @property selectedPlanet A StateFlow containing the selected planet.
 * @property isLoading A StateFlow indicating whether data is currently loading.
 */
internal interface MainActor {
    val state: StateFlow<MainState>
    val favoriteCharacters: StateFlow<Map<String, Boolean>>
    val charactersPager: StateFlow<Flow<PagingData<StarWarsCharacter>>>
    val starshipsPager: StateFlow<Flow<PagingData<Starship>>>
    val planetsPager: StateFlow<Flow<PagingData<Planet>>>
    val selectedCharacter: StateFlow<StarWarsCharacter?>
    val selectedStarship: StateFlow<Starship?>
    val selectedPlanet: StateFlow<Planet?>
    val isLoading: StateFlow<Boolean>

    /**
     * Handles the given intent and updates the state accordingly.
     *
     * @param intent The intent to handle.
     */
    suspend fun handleIntent(intent: MainAction)

    /**
     * Refreshes the data in the application.
     */
    suspend fun refreshData()

    /**
     * Fetches starships from the repository.
     *
     * @param after The cursor indicating the position after which to fetch the starships.
     * @param first The number of starships to fetch.
     * @return A StarshipsResponse containing the fetched starships and pagination info.
     */
    suspend fun fetchStarships(after: String?, first: Int): StarshipsResponse

    /**
     * Fetches planets from the repository.
     *
     * @param after The cursor indicating the position after which to fetch the planets.
     * @param first The number of planets to fetch.
     * @return A PlanetsResponse containing the fetched planets and pagination info.
     */
    suspend fun fetchPlanets(after: String?, first: Int): PlanetsResponse

    /**
     * Fetches characters from the repository.
     *
     * @param after The cursor indicating the position after which to fetch the characters.
     * @param first The number of characters to fetch.
     * @return A CharactersResponse containing the fetched characters and pagination info.
     */
    suspend fun fetchCharacters(after: String?, first: Int): CharactersResponse

    /**
     * Updates the characters in the database.
     *
     * @param characters The list of characters to update.
     */
    suspend fun updateCharactersInDatabase(characters: List<StarWarsCharacter>)

    /**
     * Updates the starships in the database.
     *
     * @param starships The list of starships to update.
     */
    suspend fun updateStarshipsInDatabase(starships: List<Starship>)

    /**
     * Updates the planets in the database.
     *
     * @param planets The list of planets to update.
     */
    suspend fun updatePlanetsInDatabase(planets: List<Planet>)
}
