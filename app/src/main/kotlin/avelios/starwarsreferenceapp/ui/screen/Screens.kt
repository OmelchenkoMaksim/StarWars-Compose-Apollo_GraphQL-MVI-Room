package avelios.starwarsreferenceapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship
import avelios.starwarsreferenceapp.ui.component.CharacterItem
import avelios.starwarsreferenceapp.ui.component.LoadingIndicator
import avelios.starwarsreferenceapp.ui.component.PlanetItem
import avelios.starwarsreferenceapp.ui.component.StarshipItem
import avelios.starwarsreferenceapp.ui.screen.CharacterDetailsScreenConstants.BIRTH_YEAR
import avelios.starwarsreferenceapp.ui.screen.CharacterDetailsScreenConstants.EYE_COLOR
import avelios.starwarsreferenceapp.ui.screen.CharacterDetailsScreenConstants.GENDER
import avelios.starwarsreferenceapp.ui.screen.CharacterDetailsScreenConstants.HAIR_COLOR
import avelios.starwarsreferenceapp.ui.screen.CharacterDetailsScreenConstants.HEIGHT
import avelios.starwarsreferenceapp.ui.screen.CharacterDetailsScreenConstants.HOMEWORLD
import avelios.starwarsreferenceapp.ui.screen.CharacterDetailsScreenConstants.MASS
import avelios.starwarsreferenceapp.ui.screen.CharacterDetailsScreenConstants.NAME_CHARACTER
import avelios.starwarsreferenceapp.ui.screen.CharacterDetailsScreenConstants.SKIN_COLOR
import avelios.starwarsreferenceapp.ui.screen.PlanetDetailsScreenConstants.CLIMATES
import avelios.starwarsreferenceapp.ui.screen.PlanetDetailsScreenConstants.DIAMETER
import avelios.starwarsreferenceapp.ui.screen.PlanetDetailsScreenConstants.GRAVITY
import avelios.starwarsreferenceapp.ui.screen.PlanetDetailsScreenConstants.NAME_PLANET
import avelios.starwarsreferenceapp.ui.screen.PlanetDetailsScreenConstants.ORBITAL_PERIOD
import avelios.starwarsreferenceapp.ui.screen.PlanetDetailsScreenConstants.POPULATION
import avelios.starwarsreferenceapp.ui.screen.PlanetDetailsScreenConstants.ROTATION_PERIOD
import avelios.starwarsreferenceapp.ui.screen.PlanetDetailsScreenConstants.SURFACE_WATER
import avelios.starwarsreferenceapp.ui.screen.PlanetDetailsScreenConstants.TERRAINS
import avelios.starwarsreferenceapp.ui.screen.StarshipDetailsScreenConstants.CREW
import avelios.starwarsreferenceapp.ui.screen.StarshipDetailsScreenConstants.HYPERDRIVE_RATING
import avelios.starwarsreferenceapp.ui.screen.StarshipDetailsScreenConstants.LENGTH
import avelios.starwarsreferenceapp.ui.screen.StarshipDetailsScreenConstants.MANUFACTURERS
import avelios.starwarsreferenceapp.ui.screen.StarshipDetailsScreenConstants.MAX_ATMOSPHERING_SPEED
import avelios.starwarsreferenceapp.ui.screen.StarshipDetailsScreenConstants.MODEL
import avelios.starwarsreferenceapp.ui.screen.StarshipDetailsScreenConstants.NAME_STARSHIP
import avelios.starwarsreferenceapp.ui.screen.StarshipDetailsScreenConstants.PASSENGERS
import avelios.starwarsreferenceapp.ui.screen.StarshipDetailsScreenConstants.STARSHIP_CLASS

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

@Composable
fun CharacterDetailsScreen(
    characterId: String,
    character: StarWarsCharacter?,
    isLoading: Boolean,
    isNetworkAvailable: Boolean,
    onFetchCharacterDetails: () -> Unit
) {
    LaunchedEffect(characterId) { onFetchCharacterDetails() }

    if (isLoading) LoadingIndicator()
    else if (!isNetworkAvailable) {
        Text(
            NO_CONNECTION,
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
    } else {
        character?.let { characterDetails: StarWarsCharacter ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            .padding(8.dp)
                    ) {
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
                    }
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
fun StarshipDetailsScreen(
    starshipId: String,
    starship: Starship?,
    isLoading: Boolean,
    isNetworkAvailable: Boolean,
    onFetchStarshipDetails: () -> Unit
) {
    LaunchedEffect(starshipId) { onFetchStarshipDetails() }

    if (isLoading) LoadingIndicator()
    else {
        if (!isNetworkAvailable) {
            Text(
                NO_CONNECTION,
                color = Color.Red, modifier = Modifier.padding(16.dp)
            )
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
}

@Composable
fun PlanetDetailsScreen(
    planetId: String,
    planet: Planet?,
    isLoading: Boolean,
    isNetworkAvailable: Boolean,
    onFetchPlanetDetails: () -> Unit
) {
    LaunchedEffect(planetId) { onFetchPlanetDetails() }

    if (isLoading) LoadingIndicator()
    else {
        if (!isNetworkAvailable) {
            Text(
                NO_CONNECTION,
                color = Color.Red, modifier = Modifier.padding(16.dp)
            )
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
}

const val TWEEN_ANIMATION_DURATION = 500
const val NO_CONNECTION = "No internet connection. Please check your connection to fetch data."

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
