package avelios.starwarsreferenceapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
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
import avelios.starwarsreferenceapp.ui.theme.ThemeVariant
import avelios.starwarsreferenceapp.ui.theme.TypographyVariant

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    showSettingsDialog: () -> Unit,
    toggleTheme: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    when (state) {
        is MainState.Loading -> {
            CircularProgressIndicator()
        }

        is MainState.DataLoaded -> {
            DataScreen(
                onFavoriteClick = { id, isFavorite ->
                    viewModel.handleIntent(MainIntent.UpdateFavoriteStatus(id, isFavorite))
                },
                showSettingsDialog = showSettingsDialog,
                toggleTheme = toggleTheme
            )
        }

        is MainState.Error -> {
            Text(text = "Error: ${(state as MainState.Error).message}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(
    onFavoriteClick: (String, Boolean) -> Unit,
    showSettingsDialog: () -> Unit,
    toggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val isDarkTheme = remember { mutableStateOf(false) }
    val showToast = rememberToast()
    val showOnlyFavorites = remember { mutableStateOf(false) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val title = when (currentBackStackEntry?.destination?.route) {
        "character_details/{characterId}" -> "Character"
        "starship_details/{starshipId}" -> "Starship"
        "planet_details/{planetId}" -> "Planet"
        else -> "Star Wars APP"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    if (currentBackStackEntry?.destination?.route in listOf(
                            "character_details/{characterId}",
                            "starship_details/{starshipId}",
                            "planet_details/{planetId}"
                        )
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (currentBackStackEntry?.destination?.route == NavigationItem.Characters.route) {
                        IconButton(onClick = { showOnlyFavorites.value = !showOnlyFavorites.value }) {
                            Icon(
                                imageVector = if (showOnlyFavorites.value) Icons.Default.Star else Icons.Default.Favorite,
                                contentDescription = "Toggle Favorites"
                            )
                        }
                    }
                    IconButton(onClick = {
                        toggleTheme()
                        val toastMessage = if (isDarkTheme.value) "Dark Mode Enabled!" else "Light Mode Enabled!"
                        showToast(toastMessage)
                        isDarkTheme.value = !isDarkTheme.value
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_day_night),
                            contentDescription = "Toggle Theme"
                        )
                    }
                    IconButton(onClick = showSettingsDialog) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        NavHost(navController, startDestination = NavigationItem.Characters.route, modifier = Modifier.padding(paddingValues)) {
            composable(NavigationItem.Characters.route) {
                CharactersScreen(
                    showOnlyFavorites = showOnlyFavorites.value,
                    onCharacterClick = { characterId ->
                        navController.navigate("character_details/$characterId")
                    },
                    onFavoriteClick = onFavoriteClick
                )
            }
            composable(NavigationItem.Starships.route) {
                StarshipsScreen(
                    onStarshipClick = { starshipId ->
                        navController.navigate("starship_details/$starshipId")
                    }
                )
            }
            composable(NavigationItem.Planets.route) {
                PlanetsScreen(
                    onPlanetClick = { planetId ->
                        navController.navigate("planet_details/$planetId")
                    }
                )
            }
            composable("character_details/{characterId}") { backStackEntry ->
                val characterId = backStackEntry.arguments?.getString("characterId") ?: return@composable
                CharacterDetailsScreen(characterId = characterId)
            }
            composable("starship_details/{starshipId}") { backStackEntry ->
                val starshipId = backStackEntry.arguments?.getString("starshipId") ?: return@composable
                StarshipDetailsScreen(starshipId = starshipId)
            }
            composable("planet_details/{planetId}") { backStackEntry ->
                val planetId = backStackEntry.arguments?.getString("planetId") ?: return@composable
                PlanetDetailsScreen(planetId = planetId)
            }
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
fun SettingsDialog(
    onDismiss: () -> Unit,
    themeVariant: MutableState<ThemeVariant>,
    typographyVariant: MutableState<TypographyVariant>,
    onSaveSettings: (ThemeVariant, TypographyVariant) -> Unit
) {
    val themeExpanded = remember { mutableStateOf(false) }
    val typographyExpanded = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column {
                Button(onClick = { themeExpanded.value = true }) {
                    OutlinedText("Select Theme (${themeVariant.value.name})")
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
                    OutlinedText("Select Typography (${typographyVariant.value.name})")
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
                    text = "OK",
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
            .padding(8.dp)
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
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
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
    data object Characters : NavigationItem("characters", Icons.Default.Person, "Characters")
    data object Starships : NavigationItem("starships", Icons.Default.Star, "Starships")
    data object Planets : NavigationItem("planets", Icons.Default.Place, "Planets")
}
