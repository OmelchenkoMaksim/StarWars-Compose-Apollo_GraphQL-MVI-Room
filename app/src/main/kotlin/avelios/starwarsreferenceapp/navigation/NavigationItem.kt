package avelios.starwarsreferenceapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * NavigationItem is a sealed class that represents different navigation destinations
 * within the application, each with its own route, icon, and title.
 *
 * @property route The route string used for navigation.
 * @property icon The ImageVector used as the icon for this navigation item.
 * @property title The title of this navigation item.
 */
sealed class NavigationItem(var route: String, var icon: ImageVector, var title: String) {

    /**
     * Navigation item representing the characters screen.
     */
    data object Characters : NavigationItem(CHARACTERS_ROUTE, Icons.Default.Person, CHARACTERS_TITLE)

    /**
     * Navigation item representing the starships screen.
     */
    data object Starships : NavigationItem(STARSHIPS_ROUTE, Icons.Default.Star, STARSHIPS_TITLE)

    /**
     * Navigation item representing the planets screen.
     */
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
