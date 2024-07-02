package avelios.starwarsreferenceapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(var route: String, var icon: ImageVector, var title: String) {
    data object Characters : NavigationItem(CHARACTERS_ROUTE, Icons.Default.Person, CHARACTERS_TITLE)
    data object Starships : NavigationItem(STARSHIPS_ROUTE, Icons.Default.Star, STARSHIPS_TITLE)
    data object Planets : NavigationItem(PLANETS_ROUTE, Icons.Default.Place, PLANETS_TITLE)
    private companion object {
        const val CHARACTERS_ROUTE = "characters"
        const val STARSHIPS_ROUTE = "starships"
        const val PLANETS_ROUTE = "planets"

        const val CHARACTERS_TITLE = "Characters"
        const val STARSHIPS_TITLE = "Starships"
        const val PLANETS_TITLE = "Planets"
    }
}
