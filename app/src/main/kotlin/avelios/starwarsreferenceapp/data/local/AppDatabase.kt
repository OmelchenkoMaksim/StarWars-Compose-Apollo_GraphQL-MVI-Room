package avelios.starwarsreferenceapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import avelios.starwarsreferenceapp.data.local.dao.CharacterDao
import avelios.starwarsreferenceapp.data.local.dao.PlanetDao
import avelios.starwarsreferenceapp.data.local.dao.StarshipDao
import avelios.starwarsreferenceapp.data.local.entity.Planet
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter
import avelios.starwarsreferenceapp.data.local.entity.Starship

/**
 * Represents the main database for the application, incorporating tables for Star Wars characters, starships, and planets.
 * Uses Room Database with converters for custom data types.
 *
 * @property characterDao Provides access to operations on the characters table.
 * @property starshipDao Provides access to operations on the starships table.
 * @property planetDao Provides access to operations on the planets table.
 */
@Database(entities = [StarWarsCharacter::class, Starship::class, Planet::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun starshipDao(): StarshipDao
    abstract fun planetDao(): PlanetDao
}
