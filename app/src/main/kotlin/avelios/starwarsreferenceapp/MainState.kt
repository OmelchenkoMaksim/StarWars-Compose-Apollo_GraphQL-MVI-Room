package avelios.starwarsreferenceapp

sealed class MainState {
    data object Loading : MainState()
    data class DataLoaded(
        val characters: List<StarWarsCharacter>,
        val starships: List<Starship>,
        val planets: List<Planet>
    ) : MainState()

    data class Error(val message: String) : MainState()
}

sealed class MainIntent {
    data object LoadData : MainIntent()
    data class UpdateFavoriteStatus(val characterId: String, val isFavorite: Boolean) : MainIntent()
    data class FetchCharacterDetails(val characterId: String) : MainIntent()
    data class FetchStarshipDetails(val starshipId: String) : MainIntent()
    data class FetchPlanetDetails(val planetId: String) : MainIntent()
}
