package avelios.starwarsreferenceapp.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship
import avelios.starwarsreferenceapp.ui.component.CharacterItem
import avelios.starwarsreferenceapp.ui.component.PlanetItem
import avelios.starwarsreferenceapp.ui.component.StarshipItem

/**
 * Displays a list of Star Wars characters with the ability to mark them as favorites.
 *
 * @param characters The list of characters to display, managed by a LazyPagingItems.
 * @param favoriteCharacters A map containing the favorite status of characters.
 * @param onCharacterClick A lambda to handle character item clicks.
 * @param onFavoriteClick A lambda to handle favorite status changes.
 * @param showOnlyFavorites A flag indicating whether to show only favorite characters.
 */
@Composable
fun CharactersScreen(
    characters: LazyPagingItems<StarWarsCharacter>,
    favoriteCharacters: Map<String, Boolean>,
    onCharacterClick: (String) -> Unit,
    onFavoriteClick: (String, Boolean) -> Unit,
    showOnlyFavorites: Boolean
) {
    LazyColumn {
        items(
            count = characters.itemCount,
            key = { index -> characters[index]?.id ?: index.toString() }
        ) { index ->
            val character = characters[index]
            character?.let {
                val isFavorite = favoriteCharacters[it.id] ?: it.isFavorite
                if (!showOnlyFavorites || isFavorite) {
                    CharacterItem(
                        character = it,
                        isFavorite = isFavorite,
                        onClick = { onCharacterClick(it.id) },
                        onFavoriteClick = { onFavoriteClick(it.id, !isFavorite) }
                    )
                }
            }
        }
    }
}

/**
 * Displays a list of Star Wars starships.
 *
 * @param starships The list of starships to display, managed by a LazyPagingItems.
 * @param onStarshipClick A lambda to handle starship item clicks.
 */
@Composable
fun StarshipsScreen(
    starships: LazyPagingItems<Starship>,
    onStarshipClick: (String) -> Unit
) {
    LazyColumn {
        items(starships.itemCount) { index ->
            val starship = starships[index]
            starship?.let {
                StarshipItem(starship = it, onClick = { onStarshipClick(it.id) })
            }
        }
    }
}

/**
 * Displays a list of Star Wars planets.
 *
 * @param planets The list of planets to display, managed by a LazyPagingItems.
 * @param onPlanetClick A lambda to handle planet item clicks.
 */
@Composable
fun PlanetsScreen(
    planets: LazyPagingItems<Planet>,
    onPlanetClick: (String) -> Unit
) {
    LazyColumn {
        items(planets.itemCount) { index ->
            val planet = planets[index]
            planet?.let {
                PlanetItem(planet = it, onClick = { onPlanetClick(it.id) })
            }
        }
    }
}
