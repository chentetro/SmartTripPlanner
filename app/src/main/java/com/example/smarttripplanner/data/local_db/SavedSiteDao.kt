package com.example.smarttripplanner.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smarttripplanner.data.model.SavedSite

@Dao
interface SavedSiteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedSite(savedSite: SavedSite): Long

    @Update
    suspend fun updateSavedSite(savedSite: SavedSite)

    @Query("DELETE FROM SavedSite WHERE siteId = :id")
    suspend fun deleteSavedSite(id: Long)

    @Query("SELECT * FROM SavedSite WHERE siteId = :id")
    fun getSavedSiteDetails(id: Long): LiveData<SavedSite>

    @Query("SELECT * FROM SavedSite WHERE tripIdOfParent = :tripId ORDER BY visitOrder ASC")
    fun getSavedSitesForTrip(tripId: Long): LiveData<List<SavedSite>>
}
