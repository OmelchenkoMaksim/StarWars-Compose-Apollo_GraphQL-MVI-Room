package avelios.starwarsreferenceapp.mvi

import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant

/**
 * Sealed class representing the various states that the app can be in.
 */
internal sealed class MainState {
    data class ShowToast(val message: String) : MainState()
    data class Error(val message: String) : MainState()
    data class DataLoaded(
        val characters: List<StarWarsCharacter>,
        val starships: List<Starship>,
        val planets: List<Planet>,
        val isNetworkAvailable: Boolean,
        val favoriteCharacters: Map<String, Boolean>,
        val themeVariant: ThemeVariant,
        val typographyVariant: TypographyVariant,
        val isDarkTheme: Boolean,
        val showOnlyFavorites: Boolean
    ) : MainState()


    data object Loading : MainState()
    data object EmptyData : MainState()
    data object ThemeChanged : MainState()
    data object NoInternetAndEmptyData : MainState()
}
