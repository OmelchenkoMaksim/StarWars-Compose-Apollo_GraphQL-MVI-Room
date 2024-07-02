package avelios.starwarsreferenceapp.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import avelios.starwarsreferenceapp.GlobalToast
import avelios.starwarsreferenceapp.R
import avelios.starwarsreferenceapp.mvi.MainAction
import avelios.starwarsreferenceapp.mvi.MainState
import avelios.starwarsreferenceapp.navigation.MainScreenConstants.EMPTY_DATA_WITHOUT_INTERNET_MESSAGE
import avelios.starwarsreferenceapp.navigation.MainScreenConstants.NO_INTERNET_AND_EMPTY_DATA_MESSAGE
import avelios.starwarsreferenceapp.navigation.MainScreenConstants.OFFLINE_MODE_MESSAGE
import avelios.starwarsreferenceapp.navigation.NavigationConstants.APP_TITLE
import avelios.starwarsreferenceapp.navigation.NavigationConstants.BACK
import avelios.starwarsreferenceapp.navigation.NavigationConstants.CHARACTER_DETAILS_ROUTE
import avelios.starwarsreferenceapp.navigation.NavigationConstants.CHARACTER_DETAILS_SLASH
import avelios.starwarsreferenceapp.navigation.NavigationConstants.CHARACTER_ID_KEY
import avelios.starwarsreferenceapp.navigation.NavigationConstants.CHARACTER_TITLE
import avelios.starwarsreferenceapp.navigation.NavigationConstants.OK
import avelios.starwarsreferenceapp.navigation.NavigationConstants.PLANET_DETAILS_ROUTE
import avelios.starwarsreferenceapp.navigation.NavigationConstants.PLANET_DETAILS_SLASH
import avelios.starwarsreferenceapp.navigation.NavigationConstants.PLANET_ID_KEY
import avelios.starwarsreferenceapp.navigation.NavigationConstants.PLANET_TITLE
import avelios.starwarsreferenceapp.navigation.NavigationConstants.SELECT_THEME
import avelios.starwarsreferenceapp.navigation.NavigationConstants.SELECT_TYPOGRAPHY
import avelios.starwarsreferenceapp.navigation.NavigationConstants.SETTINGS
import avelios.starwarsreferenceapp.navigation.NavigationConstants.STARSHIP_DETAILS_ROUTE
import avelios.starwarsreferenceapp.navigation.NavigationConstants.STARSHIP_DETAILS_SLASH
import avelios.starwarsreferenceapp.navigation.NavigationConstants.STARSHIP_ID_KEY
import avelios.starwarsreferenceapp.navigation.NavigationConstants.STARSHIP_TITLE
import avelios.starwarsreferenceapp.navigation.NavigationConstants.THEME_MODE_CHANGED
import avelios.starwarsreferenceapp.navigation.NavigationConstants.TOGGLE_FAVORITES
import avelios.starwarsreferenceapp.navigation.NavigationConstants.TOGGLE_THEME
import avelios.starwarsreferenceapp.ui.component.LoadingIndicator
import avelios.starwarsreferenceapp.ui.screen.CharacterDetailsScreen
import avelios.starwarsreferenceapp.ui.screen.CharactersScreen
import avelios.starwarsreferenceapp.ui.screen.PlanetDetailsScreen
import avelios.starwarsreferenceapp.ui.screen.PlanetsScreen
import avelios.starwarsreferenceapp.ui.screen.StarshipDetailsScreen
import avelios.starwarsreferenceapp.ui.screen.StarshipsScreen
import avelios.starwarsreferenceapp.ui.theme.StarWarsReferenceAppTheme
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant
import avelios.starwarsreferenceapp.viewmodel.MainViewModel
import timber.log.Timber

@Composable
internal fun MainScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()
    val showSettingsDialog = remember { mutableStateOf(false) }

    when (state) {
        is MainState.Loading -> LoadingIndicator()

        is MainState.DataLoaded -> {
            val dataLoadedState = state as MainState.DataLoaded
            val themeVariant = remember { mutableStateOf(dataLoadedState.themeVariant) }
            val typographyVariant = remember { mutableStateOf(dataLoadedState.typographyVariant) }
            val isDarkTheme = dataLoadedState.isDarkTheme

            StarWarsReferenceAppTheme(
                themeVariant = themeVariant.value,
                typographyVariant = typographyVariant.value,
                darkTheme = isDarkTheme
            ) {
                if (showSettingsDialog.value) {
                    SettingsDialog(
                        onDismiss = { showSettingsDialog.value = false },
                        themeVariant = themeVariant,
                        typographyVariant = typographyVariant
                    ) { newThemeVariant, newTypographyVariant ->
                        viewModel.handleIntent(
                            MainAction.UpdateThemeAndTypography(newThemeVariant, newTypographyVariant)
                        )
                        themeVariant.value = newThemeVariant
                        typographyVariant.value = newTypographyVariant
                    }
                }
                DataScreen(viewModel = viewModel, showSettingsDialog = showSettingsDialog)
            }
        }

        is MainState.Error -> {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = (state as MainState.Error).message, color = Color.Red)
            }
            Timber.e("Error state in MainScreen: ${(state as MainState.Error).message}")
        }

        MainState.EmptyData -> {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(text = EMPTY_DATA_WITHOUT_INTERNET_MESSAGE, color = Color.Gray)
            }
        }

        MainState.NoInternetAndEmptyData -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = NO_INTERNET_AND_EMPTY_DATA_MESSAGE, color = Color.Gray)
            }
        }

        is MainState.ShowToast -> {
            GlobalToast.show(LocalContext.current, (state as MainState.ShowToast).message)
        }

        MainState.ThemeChanged -> {
            GlobalToast.show(LocalContext.current, "Theme mode changed")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DataScreen(viewModel: MainViewModel, showSettingsDialog: MutableState<Boolean>) {
    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()

    val navController = rememberNavController()
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

                        IconButton(onClick = {
                            viewModel.handleIntent(MainAction.ToggleShowOnlyFavorites)
                        }) {
                            Icon(
                                imageVector = if (showOnlyFavorites.value) Icons.Default.Star else Icons.Default.Favorite,
                                contentDescription = TOGGLE_FAVORITES
                            )
                        }
                    }
                    val context = LocalContext.current
                    IconButton(onClick = {

                        GlobalToast.show(context, THEME_MODE_CHANGED)
                        viewModel.handleIntent(MainAction.ToggleTheme)
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_day_night),
                            contentDescription = TOGGLE_THEME
                        )
                    }
                    IconButton(onClick = { showSettingsDialog.value = true }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = SETTINGS)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavigationHost(
                navController = navController,
                paddingValues = paddingValues,
                viewModel = viewModel,
            )

            if (!isNetworkAvailable) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red)
                        .padding(8.dp)
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.Center
                ) {
                    Text(OFFLINE_MODE_MESSAGE, color = Color.White)
                }
            }
        }
    }
}

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
                    navController.navigate("$CHARACTER_DETAILS_SLASH$characterId")
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
                    navController.navigate("$STARSHIP_DETAILS_SLASH$starshipId")
                }
            )
        }
        composable(NavigationItem.Planets.route) {
            PlanetsScreen(
                planets = planets,
                onPlanetClick = { planetId ->
                    navController.navigate("$PLANET_DETAILS_SLASH$planetId")
                }
            )
        }
        composable(CHARACTER_DETAILS_ROUTE) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getString(CHARACTER_ID_KEY) ?: return@composable
            CharacterDetailsScreen(
                characterId = characterId,
                character = viewModel.selectedCharacter.collectAsState().value,
                isLoading = viewModel.isLoading.collectAsState().value,
                isNetworkAvailable = viewModel.isNetworkAvailable.collectAsState().value,
                onFetchCharacterDetails = { viewModel.handleIntent(MainAction.FetchCharacterDetails(characterId)) }
            )
        }
        composable(STARSHIP_DETAILS_ROUTE) { backStackEntry ->
            val starshipId = backStackEntry.arguments?.getString(STARSHIP_ID_KEY) ?: return@composable
            StarshipDetailsScreen(
                starshipId = starshipId,
                starship = viewModel.selectedStarship.collectAsState().value,
                isLoading = viewModel.isLoading.collectAsState().value,
                isNetworkAvailable = viewModel.isNetworkAvailable.collectAsState().value
            ) { viewModel.handleIntent(MainAction.FetchStarshipDetails(starshipId)) }
        }
        composable(PLANET_DETAILS_ROUTE) { backStackEntry ->
            val planetId = backStackEntry.arguments?.getString(PLANET_ID_KEY) ?: return@composable
            PlanetDetailsScreen(
                planetId = planetId,
                planet = viewModel.selectedPlanet.collectAsState().value,
                isLoading = viewModel.isLoading.collectAsState().value,
                isNetworkAvailable = viewModel.isNetworkAvailable.collectAsState().value
            ) { viewModel.handleIntent(MainAction.FetchPlanetDetails(planetId)) }
        }
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
            TextButton(onClick = {
                onSaveSettings(themeVariant.value, typographyVariant.value)
                onDismiss()
            }) {
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

object MainScreenConstants {
    const val EMPTY_DATA_WITHOUT_INTERNET_MESSAGE = "No internet connection. The lists are empty."
    const val NO_INTERNET_AND_EMPTY_DATA_MESSAGE = "No internet connection and the local database is empty."
    const val OFFLINE_MODE_MESSAGE = "Offline mode: Please check your internet connection"
}
