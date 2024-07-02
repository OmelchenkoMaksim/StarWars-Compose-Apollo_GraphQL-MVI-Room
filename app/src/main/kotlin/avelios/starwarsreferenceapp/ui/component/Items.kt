package avelios.starwarsreferenceapp.ui.component

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship
import avelios.starwarsreferenceapp.ui.screen.CharacterDetailsScreenConstants
import avelios.starwarsreferenceapp.ui.screen.PlanetDetailsScreenConstants
import avelios.starwarsreferenceapp.ui.screen.StarshipDetailsScreenConstants
import avelios.starwarsreferenceapp.ui.screen.TWEEN_ANIMATION_DURATION


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
            Text(text = "${CharacterDetailsScreenConstants.NAME_CHARACTER}${character.name}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "${CharacterDetailsScreenConstants.FILMS_COUNT}${character.filmsCount}", style = MaterialTheme.typography.bodyLarge)
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.Star,
                contentDescription = if (isFavorite) CharacterDetailsScreenConstants.REMOVE_FROM_FAVORITES else CharacterDetailsScreenConstants.ADD_TO_FAVORITES
            )
        }
    }
}

@Composable
fun StarshipItem(starship: Starship, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(12.dp).clickable(onClick = onClick)) {
        Text(text = "${StarshipDetailsScreenConstants.STARSHIP}${starship.name}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun PlanetItem(planet: Planet, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(12.dp).clickable(onClick = onClick)) {
        Text(text = "${PlanetDetailsScreenConstants.PLANET}${planet.name}", style = MaterialTheme.typography.bodyMedium)
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
