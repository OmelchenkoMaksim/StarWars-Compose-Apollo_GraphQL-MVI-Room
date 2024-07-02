package avelios.starwarsreferenceapp.mvi

import android.nfc.tech.MifareUltralight
import androidx.paging.PagingData
import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship
import avelios.starwarsreferenceapp.domain.model.CharactersResponse
import avelios.starwarsreferenceapp.domain.model.PlanetsResponse
import avelios.starwarsreferenceapp.domain.model.StarshipsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

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

    suspend fun handleIntent(intent: MainAction)
    suspend fun refreshData()

    suspend fun fetchStarships(after: String? = null, first: Int = MifareUltralight.PAGE_SIZE): StarshipsResponse
    suspend fun fetchPlanets(after: String? = null, first: Int = MifareUltralight.PAGE_SIZE): PlanetsResponse
    suspend fun fetchCharacters(after: String? = null, first: Int = MifareUltralight.PAGE_SIZE): CharactersResponse

    suspend fun updateCharactersInDatabase(characters: List<StarWarsCharacter>)
    suspend fun updateStarshipsInDatabase(starships: List<Starship>)
    suspend fun updatePlanetsInDatabase(planets: List<Planet>)
}
