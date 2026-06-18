package com.example.smarttripplanner.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.smarttripplanner.data.model.FavoriteEntity

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): LiveData<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fav: FavoriteEntity)

    @Delete
    suspend fun delete(fav: FavoriteEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: String): LiveData<Boolean>

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: String)
}