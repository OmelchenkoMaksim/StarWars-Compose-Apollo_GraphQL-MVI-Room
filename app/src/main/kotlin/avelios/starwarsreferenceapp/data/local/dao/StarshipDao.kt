package avelios.starwarsreferenceapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import avelios.starwarsreferenceapp.data.local.entity.Starship

@Dao
interface StarshipDao {
    @Query("SELECT * FROM starships")
    fun getStarshipsPagingSource(): PagingSource<Int, Starship>

    @Query("SELECT * FROM starships")
    suspend fun getAllStarships(): List<Starship>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStarships(vararg starships: Starship)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStarships(starships: List<Starship>)
}
