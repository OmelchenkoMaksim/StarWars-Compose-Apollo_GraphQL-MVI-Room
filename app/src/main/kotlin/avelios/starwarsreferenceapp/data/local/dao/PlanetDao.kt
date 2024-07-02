package avelios.starwarsreferenceapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import avelios.starwarsreferenceapp.data.local.entity.Planet

@Dao
interface PlanetDao {
    @Query("SELECT * FROM planets")
    fun getPlanetsPagingSource(): PagingSource<Int, Planet>

    @Query("SELECT * FROM planets")
    suspend fun getAllPlanets(): List<Planet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanets(vararg planets: Planet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanets(planets: List<Planet>)
}
