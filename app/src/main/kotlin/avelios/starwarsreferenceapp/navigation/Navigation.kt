package avelios.starwarsreferenceapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.paging.compose.collectAsLazyPagingItems
import avelios.starwarsreferenceapp.mvi.MainAction
import avelios.starwarsreferenceapp.mvi.MainState
import avelios.starwarsreferenceapp.ui.screens.CharacterDetailsScreen
import avelios.starwarsreferenceapp.ui.screens.CharactersScreen
import avelios.starwarsreferenceapp.ui.screens.PlanetDetailsScreen
import avelios.starwarsreferenceapp.ui.screens.PlanetsScreen
import avelios.starwarsreferenceapp.ui.screens.StarshipDetailsScreen
import avelios.starwarsreferenceapp.ui.screens.StarshipsScreen
import avelios.starwarsreferenceapp.viewmodel.MainViewModel


/**
 * NavigationHost composable function that sets up the navigation host
 * for managing different screens and their respective routes.
 *
 * @param navController The navigation controller used for navigation.
 * @param paddingValues The padding values to apply to the content.
 * @param viewModel The ViewModel associated with the NavigationHost.
 */
@Composable
internal fun NavigationHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: MainViewModel
) {
    val state by viewModel.state.collectAsState()
    val favoriteCharacters by viewModel.favoriteCharacters.collectAsState()
    val characters = viewModel.charactersPager.collectAsState().value.collectAsLazyPagingItems()
    val starships = viewModel.starshipsPager.collectAsState().value.collectAsLazyPagingItems()
    val planets = viewModel.planetsPager.collectAsState().value.collectAsLazyPagingItems()

    NavHost(
        navController, startDestination = NavigationItem.Characters.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(NavigationItem.Characters.route) {
            CharactersScreen(
                characters = characters,
                favoriteCharacters = favoriteCharacters,
                onCharacterClick = { characterId ->
                    navController.navigate("${NavigationConstants.CHARACTER_DETAILS_SLASH}$characterId")
                },
                onFavoriteClick = { id, isFavorite ->
                    viewModel.handleIntent(MainAction.UpdateFavoriteStatus(id, isFavorite))
                },
                showOnlyFavorites = (state as? MainState.DataLoaded)?.showOnlyFavorites ?: false,
            )
        }
        composable(NavigationItem.Starships.route) {
            StarshipsScreen(
                starships = starships,
                onStarshipClick = { starshipId ->
                    navController.navigate("${NavigationConstants.STARSHIP_DETAILS_SLASH}$starshipId")
                }
            )
        }
        composable(NavigationItem.Planets.route) {
            PlanetsScreen(
                planets = planets,
                onPlanetClick = { planetId ->
                    navController.navigate("${NavigationConstants.PLANET_DETAILS_SLASH}$planetId")
                }
            )
        }
        composable(NavigationConstants.CHARACTER_DETAILS_ROUTE) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString(NavigationConstants.CHARACTER_ID_KEY) ?: return@composable
            CharacterDetailsScreen(
                characterId = characterId,
                character = viewModel.selectedCharacter.collectAsState().value,
                isLoading = viewModel.isLoading.collectAsState().value,
                isNetworkAvailable = viewModel.isNetworkAvailable.collectAsState().value,
                onFetchCharacterDetails = { viewModel.handleIntent(MainAction.FetchCharacterDetails(characterId)) }
            )
        }
        composable(NavigationConstants.STARSHIP_DETAILS_ROUTE) { backStackEntry ->
            val starshipId = backStackEntry.arguments?.getString(NavigationConstants.STARSHIP_ID_KEY) ?: return@composable
            StarshipDetailsScreen(
                starshipId = starshipId,
                starship = viewModel.selectedStarship.collectAsState().value,
                isLoading = viewModel.isLoading.collectAsState().value,
                isNetworkAvailable = viewModel.isNetworkAvailable.collectAsState().value
            ) { viewModel.handleIntent(MainAction.FetchStarshipDetails(starshipId)) }
        }
        composable(NavigationConstants.PLANET_DETAILS_ROUTE) { backStackEntry ->
            val planetId = backStackEntry.arguments?.getString(NavigationConstants.PLANET_ID_KEY) ?: return@composable
            PlanetDetailsScreen(
                planetId = planetId,
                planet = viewModel.selectedPlanet.collectAsState().value,
                isLoading = viewModel.isLoading.collectAsState().value,
                isNetworkAvailable = viewModel.isNetworkAvailable.collectAsState().value
            ) { viewModel.handleIntent(MainAction.FetchPlanetDetails(planetId)) }
        }
    }
}

/**
 * BottomNavigationBar composable function to display the bottom navigation bar
 * with navigation items.
 *
 * @param navController The navigation controller used for navigation.
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem.Characters,
        NavigationItem.Starships,
        NavigationItem.Planets
    )
    NavigationBar {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.popBackStack(navController.graph.startDestinationId, false)
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * Helper function to get the current route of the navigation controller.
 *
 * @param navController The navigation controller used for navigation.
 * @return The current route.
 */
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

object NavigationConstants {
    const val CHARACTER_DETAILS_ROUTE = "character_details/{characterId}"
    const val STARSHIP_DETAILS_ROUTE = "starship_details/{starshipId}"
    const val PLANET_DETAILS_ROUTE = "planet_details/{planetId}"

    const val CHARACTER_DETAILS_SLASH = "character_details/"
    const val STARSHIP_DETAILS_SLASH = "starship_details/"
    const val PLANET_DETAILS_SLASH = "planet_details/"

    const val APP_TITLE = "Star Wars APP"
    const val CHARACTER_TITLE = "Character"
    const val STARSHIP_TITLE = "Starship"
    const val PLANET_TITLE = "Planet"

    const val THEME_MODE_CHANGED = "Theme Mode Changed!"
    const val BACK = "Back"
    const val TOGGLE_FAVORITES = "Toggle Favorites"
    const val TOGGLE_THEME = "Toggle Theme"
    const val SETTINGS = "Settings"
    const val SELECT_THEME = "Select Theme"
    const val SELECT_TYPOGRAPHY = "Select Typography"
    const val OK = "OK"

    const val CHARACTER_ID_KEY = "characterId"
    const val STARSHIP_ID_KEY = "starshipId"
    const val PLANET_ID_KEY = "planetId"
}
