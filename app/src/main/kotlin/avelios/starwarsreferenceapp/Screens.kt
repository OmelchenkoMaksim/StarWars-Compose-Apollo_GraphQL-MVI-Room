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
import androidx.compose.material3.MaterialTheme.typography
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
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.ADD_TO_FAVORITES
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.BIRTH_YEAR
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.EYE_COLOR
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.FILMS_COUNT
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.GENDER
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.HAIR_COLOR
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.HEIGHT
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.HOMEWORLD
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.MASS
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.NAME_CHARACTER
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.REMOVE_FROM_FAVORITES
import avelios.starwarsreferenceapp.CharacterDetailsScreenConstants.SKIN_COLOR
import avelios.starwarsreferenceapp.PlanetDetailsScreenConstants.CLIMATES
import avelios.starwarsreferenceapp.PlanetDetailsScreenConstants.DIAMETER
import avelios.starwarsreferenceapp.PlanetDetailsScreenConstants.GRAVITY
import avelios.starwarsreferenceapp.PlanetDetailsScreenConstants.NAME_PLANET
import avelios.starwarsreferenceapp.PlanetDetailsScreenConstants.ORBITAL_PERIOD
import avelios.starwarsreferenceapp.PlanetDetailsScreenConstants.PLANET
import avelios.starwarsreferenceapp.PlanetDetailsScreenConstants.POPULATION
import avelios.starwarsreferenceapp.PlanetDetailsScreenConstants.ROTATION_PERIOD
import avelios.starwarsreferenceapp.PlanetDetailsScreenConstants.SURFACE_WATER
import avelios.starwarsreferenceapp.PlanetDetailsScreenConstants.TERRAINS
import avelios.starwarsreferenceapp.StarshipDetailsScreenConstants.CREW
import avelios.starwarsreferenceapp.StarshipDetailsScreenConstants.HYPERDRIVE_RATING
import avelios.starwarsreferenceapp.StarshipDetailsScreenConstants.LENGTH
import avelios.starwarsreferenceapp.StarshipDetailsScreenConstants.MANUFACTURERS
import avelios.starwarsreferenceapp.StarshipDetailsScreenConstants.MAX_ATMOSPHERING_SPEED
import avelios.starwarsreferenceapp.StarshipDetailsScreenConstants.MODEL
import avelios.starwarsreferenceapp.StarshipDetailsScreenConstants.NAME_STARSHIP
import avelios.starwarsreferenceapp.StarshipDetailsScreenConstants.PASSENGERS
import avelios.starwarsreferenceapp.StarshipDetailsScreenConstants.STARSHIP
import avelios.starwarsreferenceapp.StarshipDetailsScreenConstants.STARSHIP_CLASS
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
    val starshipsPagingData by viewModel.starshipsPager.collectAsState()
    val lazyPagingItems: LazyPagingItems<Starship> = starshipsPagingData.collectAsLazyPagingItems()

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
                loadState.refresh is LoadState.Loading -> LoadingIndicator()
                loadState.append is LoadState.Loading -> LoadingIndicator()

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
    val planetsPagingData by viewModel.planetsPager.collectAsState()
    val lazyPagingItems: LazyPagingItems<Planet> = planetsPagingData.collectAsLazyPagingItems()

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
                loadState.refresh is LoadState.Loading -> LoadingIndicator()
                loadState.append is LoadState.Loading -> LoadingIndicator()

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
                        text = "${NAME_CHARACTER}${characterDetails.name}",
                        style = typography.bodyLarge.copy(
                            shadow = Shadow(
                                color = MaterialTheme.colorScheme.onBackground,
                                offset = Offset(2f, 2f),
                                blurRadius = 2f
                            )
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(text = "${BIRTH_YEAR}${characterDetails.birthYear}", style = typography.bodyMedium)
                    Text(text = "${EYE_COLOR}${characterDetails.eyeColor}", style = typography.bodyMedium)
                    Text(text = "${GENDER}${characterDetails.gender}", style = typography.bodyMedium)
                    Text(text = "${HAIR_COLOR}${characterDetails.hairColor}", style = typography.bodyMedium)
                    Text(text = "${HEIGHT}${characterDetails.height}", style = typography.bodyMedium)
                    Text(text = "${MASS}${characterDetails.mass}", style = typography.bodyMedium)
                    Text(text = "${SKIN_COLOR}${characterDetails.skinColor}", style = typography.bodyMedium)
                    Text(text = "${HOMEWORLD}${characterDetails.homeworld}", style = typography.bodyMedium)
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
                        text = "${NAME_STARSHIP}${starshipDetails.name}",
                        style = typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(text = "${MODEL}${starshipDetails.model}", style = typography.bodyMedium)
                    Text(text = "${STARSHIP_CLASS}${starshipDetails.starshipClass}", style = typography.bodyMedium)
                    Text(text = "${MANUFACTURERS}${starshipDetails.manufacturers.joinToString()}", style = typography.bodyMedium)
                    Text(text = "${LENGTH}${starshipDetails.length}", style = typography.bodyMedium)
                    Text(text = "${CREW}${starshipDetails.crew}", style = typography.bodyMedium)
                    Text(text = "${PASSENGERS}${starshipDetails.passengers}", style = typography.bodyMedium)
                    Text(text = "${MAX_ATMOSPHERING_SPEED}${starshipDetails.maxAtmospheringSpeed}", style = typography.bodyMedium)
                    Text(text = "${HYPERDRIVE_RATING}${starshipDetails.hyperdriveRating}", style = typography.bodyMedium)
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
                        text = "${NAME_PLANET}${planetDetails.name}",
                        style = typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(text = "${CLIMATES}${planetDetails.climates.joinToString()}", style = typography.bodyMedium)
                    Text(text = "${DIAMETER}${planetDetails.diameter}", style = typography.bodyMedium)
                    Text(text = "${ROTATION_PERIOD}${planetDetails.rotationPeriod}", style = typography.bodyMedium)
                    Text(text = "${ORBITAL_PERIOD}${planetDetails.orbitalPeriod}", style = typography.bodyMedium)
                    Text(text = "${GRAVITY}${planetDetails.gravity}", style = typography.bodyMedium)
                    Text(text = "${POPULATION}${planetDetails.population}", style = typography.bodyMedium)
                    Text(text = "${TERRAINS}${planetDetails.terrains.joinToString()}", style = typography.bodyMedium)
                    Text(text = "${SURFACE_WATER}${planetDetails.surfaceWater}", style = typography.bodyMedium)
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
            Text(text = "$NAME_CHARACTER${character.name}", style = typography.bodyMedium)
            Text(text = "$FILMS_COUNT${character.filmsCount}", style = typography.bodyLarge)
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.Star,
                contentDescription = if (isFavorite) REMOVE_FROM_FAVORITES else ADD_TO_FAVORITES
            )
        }
    }
}

@Composable
fun StarshipItem(starship: Starship, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(12.dp).clickable(onClick = onClick)) {
        Text(text = "$STARSHIP${starship.name}", style = typography.bodyMedium)
    }
}

@Composable
fun PlanetItem(planet: Planet, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(12.dp).clickable(onClick = onClick)) {
        Text(text = "$PLANET${planet.name}", style = typography.bodyMedium)
    }
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    strokeWidth: Dp = 4.dp
) {
    val rainbowColors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Cyan, Color.Magenta)
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val color by infiniteTransition.animateColor(
        initialValue = rainbowColors.first(),
        targetValue = rainbowColors.last(),
        animationSpec = infiniteRepeatable(tween(TWEEN_ANIMATION_DURATION), repeatMode = RepeatMode.Reverse), label = ""
    )

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(TWEEN_ANIMATION_DURATION)),
        exit = fadeOut(animationSpec = tween(TWEEN_ANIMATION_DURATION))
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

const val TWEEN_ANIMATION_DURATION = 500

object CharacterDetailsScreenConstants {
    const val NAME_CHARACTER = "Name: "
    const val BIRTH_YEAR = "Birth Year: "
    const val EYE_COLOR = "Eye Color: "
    const val GENDER = "Gender: "
    const val HAIR_COLOR = "Hair Color: "
    const val HEIGHT = "Height: "
    const val MASS = "Mass: "
    const val SKIN_COLOR = "Skin Color: "
    const val HOMEWORLD = "Homeworld: "
    const val FILMS_COUNT = "Films Count: "
    const val REMOVE_FROM_FAVORITES = "Remove from favorites"
    const val ADD_TO_FAVORITES = "Add to favorites"
}

object StarshipDetailsScreenConstants {
    const val NAME_STARSHIP = "Name: "
    const val MODEL = "Model: "
    const val STARSHIP_CLASS = "Starship Class: "
    const val MANUFACTURERS = "Manufacturers: "
    const val LENGTH = "Length: "
    const val CREW = "Crew: "
    const val PASSENGERS = "Passengers: "
    const val MAX_ATMOSPHERING_SPEED = "Max Atmosphering Speed: "
    const val HYPERDRIVE_RATING = "Hyperdrive Rating: "
    const val STARSHIP = "Starship: "
}

object PlanetDetailsScreenConstants {
    const val NAME_PLANET = "Name: "
    const val CLIMATES = "Climates: "
    const val DIAMETER = "Diameter: "
    const val ROTATION_PERIOD = "Rotation Period: "
    const val ORBITAL_PERIOD = "Orbital Period: "
    const val GRAVITY = "Gravity: "
    const val POPULATION = "Population: "
    const val TERRAINS = "Terrains: "
    const val SURFACE_WATER = "Surface Water: "
    const val PLANET = "Planet: "
}
