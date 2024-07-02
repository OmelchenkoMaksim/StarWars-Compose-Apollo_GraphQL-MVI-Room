package avelios.starwarsreferenceapp.mvi

import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant

internal sealed class MainState {
    data object Loading : MainState()
    data class DataLoaded(
        val characters: List<StarWarsCharacter> = emptyList(),
        val starships: List<Starship> = emptyList(),
        val planets: List<Planet> = emptyList(),
        val isNetworkAvailable: Boolean = false,
        val favoriteCharacters: Map<String, Boolean> = emptyMap(),
        val themeVariant: ThemeVariant = ThemeVariant.MorningMystic,
        val typographyVariant: TypographyVariant = TypographyVariant.Classic,
        val isDarkTheme: Boolean = false,
        val showOnlyFavorites: Boolean = false
    ) : MainState()

    data object EmptyData : MainState()
    data class ShowToast(val message: String) : MainState()
    data object ThemeChanged : MainState()
    data object NoInternetAndEmptyData : MainState()
    data class Error(val message: String) : MainState()
}
