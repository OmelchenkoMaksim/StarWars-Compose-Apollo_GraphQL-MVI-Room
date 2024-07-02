package avelios.starwarsreferenceapp.data.repository

import android.nfc.tech.MifareUltralight
import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship
import avelios.starwarsreferenceapp.domain.model.CharactersResponse
import avelios.starwarsreferenceapp.domain.model.PlanetsResponse
import avelios.starwarsreferenceapp.domain.model.StarshipsResponse

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
    suspend fun fetchCharacters(after: String? = null, first: Int = MifareUltralight.PAGE_SIZE): CharactersResponse
    suspend fun fetchStarships(after: String? = null, first: Int = MifareUltralight.PAGE_SIZE): StarshipsResponse
    suspend fun fetchPlanets(after: String? = null, first: Int = MifareUltralight.PAGE_SIZE): PlanetsResponse
    suspend fun refreshData()
}
