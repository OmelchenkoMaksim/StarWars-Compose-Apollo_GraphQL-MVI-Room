package avelios.starwarsreferenceapp.mvi

import avelios.starwarsreferenceapp.ui.theme.ThemeVariant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant

/**
 * Sealed class representing the various actions that can be performed in the app.
 */
internal sealed class MainAction {
    data object LoadData : MainAction()
    data object RefreshData : MainAction()
    data object ToggleTheme : MainAction()
    data object ToggleShowOnlyFavorites : MainAction()

    data class UpdateFavoriteStatus(val characterId: String, val isFavorite: Boolean) : MainAction()
    data class FetchCharacterDetails(val characterId: String) : MainAction()
    data class FetchStarshipDetails(val starshipId: String) : MainAction()
    data class FetchPlanetDetails(val planetId: String) : MainAction()
    data class UpdateThemeAndTypography(
        val themeVariant: ThemeVariant,
        val typographyVariant: TypographyVariant
    ) : MainAction()
}
