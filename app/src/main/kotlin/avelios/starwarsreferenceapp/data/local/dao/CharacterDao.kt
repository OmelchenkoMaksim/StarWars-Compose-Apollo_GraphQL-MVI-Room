package avelios.starwarsreferenceapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import avelios.starwarsreferenceapp.data.local.entity.StarWarsCharacter

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters")
    fun getCharactersPagingSource(): PagingSource<Int, StarWarsCharacter>

    @Query("SELECT * FROM characters")
    suspend fun getAllCharacters(): List<StarWarsCharacter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(vararg characters: StarWarsCharacter)

    @Query("UPDATE characters SET isFavorite = :isFavorite WHERE id = :characterId")
    suspend fun updateFavoriteStatus(characterId: String, isFavorite: Boolean)

    @Query("SELECT isFavorite FROM characters WHERE id = :characterId")
    suspend fun getFavoriteStatus(characterId: String): Boolean?
}
