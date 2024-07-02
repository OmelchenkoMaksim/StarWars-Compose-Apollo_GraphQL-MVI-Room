package avelios.starwarsreferenceapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import avelios.starwarsreferenceapp.NavigationConstants.APP_TITLE
import avelios.starwarsreferenceapp.NavigationConstants.BACK
import avelios.starwarsreferenceapp.NavigationConstants.CHARACTER_DETAILS_ROUTE
import avelios.starwarsreferenceapp.NavigationConstants.CHARACTER_DETAILS_SLASH
import avelios.starwarsreferenceapp.NavigationConstants.CHARACTER_ID_KEY
import avelios.starwarsreferenceapp.NavigationConstants.CHARACTER_TITLE
import avelios.starwarsreferenceapp.NavigationConstants.DARK_MODE_ENABLED
import avelios.starwarsreferenceapp.NavigationConstants.LIGHT_MODE_ENABLED
import avelios.starwarsreferenceapp.NavigationConstants.OK
import avelios.starwarsreferenceapp.NavigationConstants.PLANET_DETAILS_ROUTE
import avelios.starwarsreferenceapp.NavigationConstants.PLANET_DETAILS_SLASH
import avelios.starwarsreferenceapp.NavigationConstants.PLANET_ID_KEY
import avelios.starwarsreferenceapp.NavigationConstants.PLANET_TITLE
import avelios.starwarsreferenceapp.NavigationConstants.SELECT_THEME
import avelios.starwarsreferenceapp.NavigationConstants.SELECT_TYPOGRAPHY
import avelios.starwarsreferenceapp.NavigationConstants.SETTINGS
import avelios.starwarsreferenceapp.NavigationConstants.STARSHIP_DETAILS_ROUTE
import avelios.starwarsreferenceapp.NavigationConstants.STARSHIP_DETAILS_SLASH
import avelios.starwarsreferenceapp.NavigationConstants.STARSHIP_ID_KEY
import avelios.starwarsreferenceapp.NavigationConstants.STARSHIP_TITLE
import avelios.starwarsreferenceapp.NavigationConstants.TOGGLE_FAVORITES
import avelios.starwarsreferenceapp.NavigationConstants.TOGGLE_THEME
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
internal fun MainScreen(
    viewModel: MainViewModel,
    showSettingsDialog: () -> Unit,
    toggleTheme: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val favoriteCharacters by viewModel.favoriteCharacters.collectAsState()
    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
    val charactersPager by viewModel.charactersPager.collectAsState()

    when (state) {
        is MainState.Loading -> {
            Timber.d("MainState.Loading state")
            LoadingIndicator()
        }

        is MainState.DataLoaded -> {
            Timber.d("MainState.DataLoaded state with characters: ${(state as MainState.DataLoaded).characters.size}")
            val characters = charactersPager.collectAsLazyPagingItems()

            DataScreen(
                onFavoriteClick = { id, isFavorite ->
                    viewModel.viewModelScope.launch {
                        Timber.d("Updating favorite status for character ID: $id to $isFavorite")
                        viewModel.updateFavoriteStatus(id, isFavorite)
                    }
                },
                showSettingsDialog = showSettingsDialog,
                toggleTheme = toggleTheme,
                characters = characters,
                favoriteCharacters = favoriteCharacters
            )
        }

        is MainState.EmptyData -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isNetworkAvailable) {
                        "The lists are empty. Try refreshing the data."
                    } else {
                        "No internet connection. The lists are empty."
                    }
                )
            }
        }

        is MainState.NoInternetAndEmptyData -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No internet connection and the local database is empty.")
            }
        }

        is MainState.Error -> {
            val errorMessage = (state as MainState.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $errorMessage")
            }
            Timber.e("Error state with message: $errorMessage")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(
    onFavoriteClick: (String, Boolean) -> Unit,
    showSettingsDialog: () -> Unit,
    toggleTheme: () -> Unit,
    characters: LazyPagingItems<StarWarsCharacter>,
    favoriteCharacters: Map<String, Boolean>
) {
    val navController = rememberNavController()
    val isDarkTheme = remember { mutableStateOf(false) }
    val showToast = rememberToast()
    val showOnlyFavorites = remember { mutableStateOf(false) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val title = when (currentBackStackEntry?.destination?.route) {
        CHARACTER_DETAILS_ROUTE -> CHARACTER_TITLE
        STARSHIP_DETAILS_ROUTE -> STARSHIP_TITLE
        PLANET_DETAILS_ROUTE -> PLANET_TITLE
        else -> APP_TITLE
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    if (currentBackStackEntry?.destination?.route in listOf(
                            CHARACTER_DETAILS_ROUTE,
                            STARSHIP_DETAILS_ROUTE,
                            PLANET_DETAILS_ROUTE
                        )
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = BACK)
                        }
                    }
                },
                actions = {
                    if (currentBackStackEntry?.destination?.route == NavigationItem.Characters.route) {
                        IconButton(onClick = { showOnlyFavorites.value = !showOnlyFavorites.value }) {
                            Icon(
                                imageVector =
                                if (showOnlyFavorites.value) Icons.Default.Star else Icons.Default.Favorite,
                                contentDescription = TOGGLE_FAVORITES
                            )
                        }
                    }
                    IconButton(onClick = {
                        toggleTheme()
                        isDarkTheme.value = !isDarkTheme.value
                        val toastMessage = if (isDarkTheme.value) DARK_MODE_ENABLED else LIGHT_MODE_ENABLED
                        showToast(toastMessage)
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_day_night),
                            contentDescription = TOGGLE_THEME
                        )
                    }
                    IconButton(onClick = showSettingsDialog) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = SETTINGS)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        NavigationHost(
            navController = navController,
            paddingValues = paddingValues,
            onFavoriteClick = onFavoriteClick,
            showOnlyFavorites = showOnlyFavorites.value,
            characters = characters,
            favoriteCharacters = favoriteCharacters
        )
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onFavoriteClick: (String, Boolean) -> Unit,
    showOnlyFavorites: Boolean,
    characters: LazyPagingItems<StarWarsCharacter>,
    favoriteCharacters: Map<String, Boolean>
) {
    NavHost(
        navController, startDestination = NavigationItem.Characters.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(NavigationItem.Characters.route) {
            CharactersScreen(
                showOnlyFavorites = showOnlyFavorites,
                onCharacterClick = { characterId ->
                    navController.navigate("$CHARACTER_DETAILS_SLASH$characterId")
                },
                onFavoriteClick = onFavoriteClick,
                characters = characters,
                favoriteCharacters = favoriteCharacters
            )
        }
        composable(NavigationItem.Starships.route) {
            StarshipsScreen(
                onStarshipClick = { starshipId ->
                    navController.navigate("$STARSHIP_DETAILS_SLASH$starshipId")
                }
            )
        }
        composable(NavigationItem.Planets.route) {
            PlanetsScreen(
                onPlanetClick = { planetId ->
                    navController.navigate("$PLANET_DETAILS_SLASH$planetId")
                }
            )
        }
        composable(CHARACTER_DETAILS_ROUTE) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString(CHARACTER_ID_KEY) ?: return@composable
            CharacterDetailsScreen(characterId = characterId)
        }
        composable(STARSHIP_DETAILS_ROUTE) { backStackEntry ->
            val starshipId = backStackEntry.arguments?.getString(STARSHIP_ID_KEY) ?: return@composable
            StarshipDetailsScreen(starshipId = starshipId)
        }
        composable(PLANET_DETAILS_ROUTE) { backStackEntry ->
            val planetId = backStackEntry.arguments?.getString(PLANET_ID_KEY) ?: return@composable
            PlanetDetailsScreen(planetId = planetId)
        }
    }
}

@Composable
fun rememberToast(): (String) -> Unit {
    val context = LocalContext.current
    var toast by remember { mutableStateOf<Toast?>(null) }

    return { message: String ->
        toast?.cancel()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast?.show()
    }
}

@Composable
internal fun SettingsDialog(
    onDismiss: () -> Unit,
    themeVariant: MutableState<ThemeVariant>,
    typographyVariant: MutableState<TypographyVariant>,
    onSaveSettings: (ThemeVariant, TypographyVariant) -> Unit
) {
    val themeExpanded = remember { mutableStateOf(false) }
    val typographyExpanded = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(SETTINGS) },
        text = {
            Column {
                Button(onClick = { themeExpanded.value = true }) {
                    OutlinedText("$SELECT_THEME (${themeVariant.value.name})")
                }
                DropdownMenu(
                    expanded = themeExpanded.value,
                    onDismissRequest = { themeExpanded.value = false }
                ) {
                    ThemeVariant.entries.forEach { variant ->
                        DropdownMenuItem(onClick = {
                            themeVariant.value = variant
                            themeExpanded.value = false
                            onSaveSettings(themeVariant.value, typographyVariant.value)
                        }, text = {
                            Text(variant.name)
                        })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { typographyExpanded.value = true }) {
                    OutlinedText("$SELECT_TYPOGRAPHY (${typographyVariant.value.name})")
                }
                DropdownMenu(
                    expanded = typographyExpanded.value,
                    onDismissRequest = { typographyExpanded.value = false }
                ) {
                    TypographyVariant.entries.forEach { variant ->
                        DropdownMenuItem(onClick = {
                            typographyVariant.value = variant
                            typographyExpanded.value = false
                            onSaveSettings(themeVariant.value, typographyVariant.value)
                        }, text = {
                            Text(variant.name)
                        })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = OK,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        shadow = Shadow(
                            color = MaterialTheme.colorScheme.inverseSurface,
                            offset = Offset(2f, 2f),
                            blurRadius = 2f
                        )
                    )
                )
            }
        }
    )
}

@Composable
fun OutlinedText(text: String) {
    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    Text(
        text = text,
        style = typography.bodyMedium.copy(
            color = colors.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            shadow = Shadow(
                color = colors.background,
                offset = Offset(2f, 2f),
                blurRadius = 2f
            )
        ),
        modifier = Modifier
            .padding(12.dp)
    )
}

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

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

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

    const val DARK_MODE_ENABLED = "Dark Mode Enabled!"
    const val LIGHT_MODE_ENABLED = "Light Mode Enabled!"
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
