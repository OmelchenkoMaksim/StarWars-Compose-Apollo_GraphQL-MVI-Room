package avelios.starwarsreferenceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "starships")
data class Starship(
    @PrimaryKey val id: String,
    val name: String,
    val model: String,
    val starshipClass: String,
    val manufacturers: List<String>,
    val length: Float,
    val crew: String,
    val passengers: String,
    val maxAtmospheringSpeed: Int,
    val hyperdriveRating: Float
)
