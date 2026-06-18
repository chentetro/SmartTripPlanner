package com.example.smarttripplanner.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.data.model.TripWithSites

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Update
    suspend fun updateTrip(trip: Trip)

    @Query("UPDATE trips SET is_favorite = :isFavorite WHERE trip_id = :tripId")
    suspend fun updateFavoriteStatus(tripId: Long, isFavorite: Boolean)

    @Query("SELECT * FROM trips")
    fun getAllTrips(): LiveData<List<Trip>>

    @Query("SELECT * FROM trips WHERE is_favorite = 1")
    fun getFavoriteTrips(): LiveData<List<Trip>>

    @Query("SELECT * FROM trips WHERE trip_id = :tripId")
    fun getTripById(tripId: Long): LiveData<Trip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedSite(savedSite: SavedSite): Long

    @Update
    suspend fun updateSavedSite(savedSite: SavedSite)

    @Query("DELETE FROM SavedSite WHERE siteId = :id")
    suspend fun deleteSavedSite(id: Long)

    @Query("SELECT * FROM SavedSite WHERE tripIdOfParent = :tripId ORDER BY visitOrder ASC")
    fun getSavedSitesForTrip(tripId: Long): LiveData<List<SavedSite>>

    @Transaction
    @Query("SELECT * FROM trips WHERE trip_id = :tripId")
    fun getTripWithSites(tripId: Long): LiveData<TripWithSites>
}