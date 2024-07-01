package avelios.starwarsreferenceapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharactersScreen(
    showOnlyFavorites: Boolean,
    onCharacterClick: (String) -> Unit,
    onFavoriteClick: (String, Boolean) -> Unit
) {
    val viewModel: MainViewModel = koinViewModel()
    val lazyPagingItems: LazyPagingItems<StarWarsCharacter> = viewModel.charactersPager.collectAsLazyPagingItems()

    val filteredItems = if (showOnlyFavorites) {
        lazyPagingItems.itemSnapshotList.items.filter { it.isFavorite }
    } else {
        lazyPagingItems.itemSnapshotList.items
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (filteredItems.isEmpty()) {
            Text(
                text = if (showOnlyFavorites) "No favorites found" else "No characters found",
                modifier = Modifier.align(Alignment.TopCenter).padding(8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn {
                items(filteredItems) { character ->
                    character.let {
                        CharacterItem(character = it, onClick = { onCharacterClick(it.id) }) {
                            onFavoriteClick(it.id, !it.isFavorite)
                        }
                    }
                }
            }
        }

        lazyPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                loadState.append is LoadState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                loadState.refresh is LoadState.Error -> {
                    val e = lazyPagingItems.loadState.refresh as LoadState.Error
                    Text(text = "Error: ${e.error.localizedMessage}", modifier = Modifier.align(Alignment.Center))
                }

                loadState.append is LoadState.Error -> {
                    val e = lazyPagingItems.loadState.append as LoadState.Error
                    Text(text = "Error: ${e.error.localizedMessage}", modifier = Modifier.align(Alignment.Center))
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
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                loadState.append is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp))
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
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                loadState.append is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp))
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
        CircularProgressIndicator()
    } else {
        character?.let { characterDetails: StarWarsCharacter ->
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Name: ${characterDetails.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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
        CircularProgressIndicator()
    } else {
        starship?.let { starshipDetails: Starship ->
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Name: ${starshipDetails.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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
        CircularProgressIndicator()
    } else {
        planet?.let { planetDetails: Planet ->
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Name: ${planetDetails.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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
fun CharacterItem(character: StarWarsCharacter, onClick: () -> Unit, onFavoriteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
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
                imageVector = if (character.isFavorite) Icons.Default.Favorite else Icons.Default.Star,
                contentDescription = if (character.isFavorite) "Remove from favorites" else "Add to favorites"
            )
        }
    }
}

@Composable
fun StarshipItem(starship: Starship, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(8.dp).clickable(onClick = onClick)) {
        Text(text = "Starship: ${starship.name}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun PlanetItem(planet: Planet, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(8.dp).clickable(onClick = onClick)) {
        Text(text = "Planet: ${planet.name}", style = MaterialTheme.typography.bodyMedium)
    }
}
