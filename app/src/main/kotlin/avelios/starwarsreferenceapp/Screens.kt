package avelios.starwarsreferenceapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharactersScreen(
    showOnlyFavorites: Boolean,
    onCharacterClick: (String) -> Unit,
    onFavoriteClick: (String, Boolean) -> Unit,
    characters: LazyPagingItems<StarWarsCharacter>,
    favoriteCharacters: Map<String, Boolean>
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

@Composable
fun StarshipsScreen(
    onStarshipClick: (String) -> Unit
) {
    val viewModel: MainViewModel = koinViewModel()
    val lazyPagingItems: LazyPagingItems<Starship> = viewModel.starshipsPager.collectAsLazyPagingItems()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(lazyPagingItems.itemCount) { index ->
                val starship = lazyPagingItems[index]
                starship?.let {
                    StarshipItem(starship = it, onClick = { onStarshipClick(it.id) })
                }
            }
        }

        lazyPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    LoadingIndicator()
                }

                loadState.append is LoadState.Loading -> {
                    LoadingIndicator()
                }

                loadState.refresh is LoadState.Error -> {
                    val e = lazyPagingItems.loadState.refresh as LoadState.Error
                    Text(text = "Error: ${e.error.localizedMessage}")
                }

                loadState.append is LoadState.Error -> {
                    val e = lazyPagingItems.loadState.append as LoadState.Error
                    Text(text = "Error: ${e.error.localizedMessage}")
                }
            }
        }
    }
}

@Composable
fun PlanetsScreen(
    onPlanetClick: (String) -> Unit
) {
    val viewModel: MainViewModel = koinViewModel()
    val lazyPagingItems: LazyPagingItems<Planet> = viewModel.planetsPager.collectAsLazyPagingItems()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(lazyPagingItems.itemCount) { index ->
                val planet = lazyPagingItems[index]
                planet?.let {
                    PlanetItem(planet = it, onClick = { onPlanetClick(it.id) })
                }
            }
        }

        lazyPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    LoadingIndicator()
                }

                loadState.append is LoadState.Loading -> {
                    LoadingIndicator()
                }

                loadState.refresh is LoadState.Error -> {
                    val e = lazyPagingItems.loadState.refresh as LoadState.Error
                    Text(text = "Error: ${e.error.localizedMessage}")
                }

                loadState.append is LoadState.Error -> {
                    val e = lazyPagingItems.loadState.append as LoadState.Error
                    Text(text = "Error: ${e.error.localizedMessage}")
                }
            }
        }
    }
}

@Composable
fun CharacterDetailsScreen(characterId: String) {
    val viewModel: MainViewModel = koinViewModel()
    val character by viewModel.selectedCharacter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(characterId) {
        viewModel.handleIntent(MainIntent.FetchCharacterDetails(characterId))
    }

    if (isLoading) {
        LoadingIndicator()
    } else {
        character?.let { characterDetails: StarWarsCharacter ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Name: ${characterDetails.name}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            shadow = Shadow(
                                color = MaterialTheme.colorScheme.onBackground,
                                offset = Offset(2f, 2f),
                                blurRadius = 2f
                            )
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(text = "Birth Year: ${characterDetails.birthYear}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Eye Color: ${characterDetails.eyeColor}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Gender: ${characterDetails.gender}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Hair Color: ${characterDetails.hairColor}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Height: ${characterDetails.height}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Mass: ${characterDetails.mass}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Skin Color: ${characterDetails.skinColor}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Homeworld: ${characterDetails.homeworld}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun StarshipDetailsScreen(starshipId: String) {
    val viewModel: MainViewModel = koinViewModel()
    val starship by viewModel.selectedStarship.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(starshipId) {
        viewModel.handleIntent(MainIntent.FetchStarshipDetails(starshipId))
    }

    if (isLoading) {
        LoadingIndicator()
    } else {
        starship?.let { starshipDetails: Starship ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Name: ${starshipDetails.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(text = "Model: ${starshipDetails.model}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Starship Class: ${starshipDetails.starshipClass}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Manufacturers: ${starshipDetails.manufacturers.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Length: ${starshipDetails.length}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Crew: ${starshipDetails.crew}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Passengers: ${starshipDetails.passengers}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Max Atmosphering Speed: ${starshipDetails.maxAtmospheringSpeed}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Hyperdrive Rating: ${starshipDetails.hyperdriveRating}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun PlanetDetailsScreen(planetId: String) {
    val viewModel: MainViewModel = koinViewModel()
    val planet by viewModel.selectedPlanet.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(planetId) {
        viewModel.handleIntent(MainIntent.FetchPlanetDetails(planetId))
    }

    if (isLoading) {
        LoadingIndicator()
    } else {
        planet?.let { planetDetails: Planet ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Name: ${planetDetails.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(text = "Climates: ${planetDetails.climates.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Diameter: ${planetDetails.diameter}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Rotation Period: ${planetDetails.rotationPeriod}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Orbital Period: ${planetDetails.orbitalPeriod}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Gravity: ${planetDetails.gravity}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Population: ${planetDetails.population}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Terrains: ${planetDetails.terrains.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Surface Water: ${planetDetails.surfaceWater}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun CharacterItem(
    character: StarWarsCharacter,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Name: ${character.name}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Films Count: ${character.filmsCount}", style = MaterialTheme.typography.bodyLarge)
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.Star,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
            )
        }
    }
}

@Composable
fun StarshipItem(starship: Starship, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(12.dp).clickable(onClick = onClick)) {
        Text(text = "Starship: ${starship.name}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun PlanetItem(planet: Planet, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(12.dp).clickable(onClick = onClick)) {
        Text(text = "Planet: ${planet.name}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    strokeWidth: Dp = 4.dp
) {
    val rainbowColors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Cyan, Color.Magenta)
    val infiniteTransition = rememberInfiniteTransition()
    val color by infiniteTransition.animateColor(
        initialValue = rainbowColors.first(),
        targetValue = rainbowColors.last(),
        animationSpec = infiniteRepeatable(tween(400), repeatMode = RepeatMode.Reverse), label = ""
    )

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Box(
            modifier = modifier
                .background(Color.White.copy(alpha = 0.6f))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = color,
                strokeWidth = strokeWidth,
                modifier = Modifier
                    .size(size)
                    .border(
                        width = 2.dp,
                        color = color,
                        shape = CircleShape
                    )
            )
        }
    }
}