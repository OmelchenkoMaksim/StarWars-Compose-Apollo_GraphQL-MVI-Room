package avelios.starwarsreferenceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class StarWarsCharacter(
    @PrimaryKey val id: String,
    val name: String,
    val filmsCount: Int,
    val birthYear: String,
    val eyeColor: String,
    val gender: String,
    val hairColor: String,
    val height: Int,
    val mass: Double,
    val skinColor: String,
    val homeworld: String?,
    var isFavorite: Boolean = false
)
