package avelios.starwarsreferenceapp

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [StarWarsCharacter::class, Starship::class, Planet::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun starshipDao(): StarshipDao
    abstract fun planetDao(): PlanetDao
}

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

@Entity(tableName = "planets")
data class Planet(
    @PrimaryKey val id: String,
    val name: String,
    val climates: List<String>,
    val diameter: Int,
    val rotationPeriod: Int,
    val orbitalPeriod: Int,
    val gravity: String,
    val population: Double,
    val terrains: List<String>,
    val surfaceWater: Double
)

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters")
    suspend fun getAllCharacters(): List<StarWarsCharacter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(vararg characters: StarWarsCharacter)

    @Query("UPDATE characters SET isFavorite = :isFavorite WHERE id = :characterId")
    suspend fun updateFavoriteStatus(characterId: String, isFavorite: Boolean)

    @Query("SELECT isFavorite FROM characters WHERE id = :characterId")
    suspend fun getFavoriteStatus(characterId: String): Boolean?
}

@Dao
interface StarshipDao {
    @Query("SELECT * FROM starships")
    suspend fun getAllStarships(): List<Starship>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStarships(vararg starships: Starship)
}

@Dao
interface PlanetDao {
    @Query("SELECT * FROM planets")
    suspend fun getAllPlanets(): List<Planet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanets(vararg planets: Planet)
}

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }
}
