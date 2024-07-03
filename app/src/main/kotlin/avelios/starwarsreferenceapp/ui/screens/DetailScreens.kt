package avelios.starwarsreferenceapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship
import avelios.starwarsreferenceapp.ui.component.LoadingIndicator
import avelios.starwarsreferenceapp.ui.screens.CharacterDetailsScreenConstants.BIRTH_YEAR
import avelios.starwarsreferenceapp.ui.screens.CharacterDetailsScreenConstants.EYE_COLOR
import avelios.starwarsreferenceapp.ui.screens.CharacterDetailsScreenConstants.GENDER
import avelios.starwarsreferenceapp.ui.screens.CharacterDetailsScreenConstants.HAIR_COLOR
import avelios.starwarsreferenceapp.ui.screens.CharacterDetailsScreenConstants.HEIGHT
import avelios.starwarsreferenceapp.ui.screens.CharacterDetailsScreenConstants.HOMEWORLD
import avelios.starwarsreferenceapp.ui.screens.CharacterDetailsScreenConstants.MASS
import avelios.starwarsreferenceapp.ui.screens.CharacterDetailsScreenConstants.NAME_CHARACTER
import avelios.starwarsreferenceapp.ui.screens.CharacterDetailsScreenConstants.SKIN_COLOR
import avelios.starwarsreferenceapp.ui.screens.PlanetDetailsScreenConstants.CLIMATES
import avelios.starwarsreferenceapp.ui.screens.PlanetDetailsScreenConstants.DIAMETER
import avelios.starwarsreferenceapp.ui.screens.PlanetDetailsScreenConstants.GRAVITY
import avelios.starwarsreferenceapp.ui.screens.PlanetDetailsScreenConstants.NAME_PLANET
import avelios.starwarsreferenceapp.ui.screens.PlanetDetailsScreenConstants.ORBITAL_PERIOD
import avelios.starwarsreferenceapp.ui.screens.PlanetDetailsScreenConstants.POPULATION
import avelios.starwarsreferenceapp.ui.screens.PlanetDetailsScreenConstants.ROTATION_PERIOD
import avelios.starwarsreferenceapp.ui.screens.PlanetDetailsScreenConstants.SURFACE_WATER
import avelios.starwarsreferenceapp.ui.screens.PlanetDetailsScreenConstants.TERRAINS
import avelios.starwarsreferenceapp.ui.screens.StarshipDetailsScreenConstants.CREW
import avelios.starwarsreferenceapp.ui.screens.StarshipDetailsScreenConstants.HYPERDRIVE_RATING
import avelios.starwarsreferenceapp.ui.screens.StarshipDetailsScreenConstants.LENGTH
import avelios.starwarsreferenceapp.ui.screens.StarshipDetailsScreenConstants.MANUFACTURERS
import avelios.starwarsreferenceapp.ui.screens.StarshipDetailsScreenConstants.MAX_ATMOSPHERING_SPEED
import avelios.starwarsreferenceapp.ui.screens.StarshipDetailsScreenConstants.MODEL
import avelios.starwarsreferenceapp.ui.screens.StarshipDetailsScreenConstants.NAME_STARSHIP
import avelios.starwarsreferenceapp.ui.screens.StarshipDetailsScreenConstants.PASSENGERS
import avelios.starwarsreferenceapp.ui.screens.StarshipDetailsScreenConstants.STARSHIP_CLASS

/**
 * Displays the details of a specific Star Wars character.
 *
 * @param characterId The ID of the character to display.
 * @param character The character details to display.
 * @param isLoading A flag indicating whether the data is loading.
 * @param isNetworkAvailable A flag indicating whether the network is available.
 * @param onFetchCharacterDetails A lambda to fetch the character details.
 */
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
                            style = MaterialTheme.typography.bodyLarge.copy(
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
                    Text(text = "${BIRTH_YEAR}${characterDetails.birthYear}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${EYE_COLOR}${characterDetails.eyeColor}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${GENDER}${characterDetails.gender}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${HAIR_COLOR}${characterDetails.hairColor}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${HEIGHT}${characterDetails.height}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${MASS}${characterDetails.mass}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${SKIN_COLOR}${characterDetails.skinColor}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${HOMEWORLD}${characterDetails.homeworld}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

/**
 * Displays the details of a specific Star Wars starship.
 *
 * @param starshipId The ID of the starship to display.
 * @param starship The starship details to display.
 * @param isLoading A flag indicating whether the data is loading.
 * @param isNetworkAvailable A flag indicating whether the network is available.
 * @param onFetchStarshipDetails A lambda to fetch the starship details.
 */
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
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Text(text = "${MODEL}${starshipDetails.model}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${STARSHIP_CLASS}${starshipDetails.starshipClass}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${MANUFACTURERS}${starshipDetails.manufacturers.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${LENGTH}${starshipDetails.length}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${CREW}${starshipDetails.crew}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${PASSENGERS}${starshipDetails.passengers}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${MAX_ATMOSPHERING_SPEED}${starshipDetails.maxAtmospheringSpeed}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${HYPERDRIVE_RATING}${starshipDetails.hyperdriveRating}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

/**
 * Displays the details of a specific Star Wars planet.
 *
 * @param planetId The ID of the planet to display.
 * @param planet The planet details to display.
 * @param isLoading A flag indicating whether the data is loading.
 * @param isNetworkAvailable A flag indicating whether the network is available.
 * @param onFetchPlanetDetails A lambda to fetch the planet details.
 */
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
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Text(text = "${CLIMATES}${planetDetails.climates.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${DIAMETER}${planetDetails.diameter}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${ROTATION_PERIOD}${planetDetails.rotationPeriod}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${ORBITAL_PERIOD}${planetDetails.orbitalPeriod}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${GRAVITY}${planetDetails.gravity}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${POPULATION}${planetDetails.population}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${TERRAINS}${planetDetails.terrains.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${SURFACE_WATER}${planetDetails.surfaceWater}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

const val TWEEN_ANIMATION_DURATION = 500
const val NO_CONNECTION = "No internet connection. Please check your connection to fetch data."

internal object CharacterDetailsScreenConstants {
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

internal object StarshipDetailsScreenConstants {
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

internal object PlanetDetailsScreenConstants {
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
