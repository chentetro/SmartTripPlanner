package com.example.smarttripplanner.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.smarttripplanner.data.local_db.TripDao
import com.example.smarttripplanner.data.local_db.TripPlannerDatabase
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.data.model.TripWithSites

class TripRepository(application: Application) {

    private val tripDao: TripDao

    init {
        val db = TripPlannerDatabase.getDatabase(application.applicationContext)
        tripDao = db.tripDao()
    }

    fun getAllTrips(): LiveData<List<Trip>> = tripDao.getAllTrips()

    fun getTripById(tripId: Long): LiveData<Trip> = tripDao.getTripById(tripId)

    fun getFavoriteTrips(): LiveData<List<Trip>> = tripDao.getFavoriteTrips()

    suspend fun insertTrip(trip: Trip): Long = tripDao.insertTrip(trip)

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    suspend fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip)
    }

    suspend fun updateFavoriteStatus(tripId: Long, isFavorite: Boolean) {
        tripDao.updateFavoriteStatus(tripId, isFavorite)
    }

    suspend fun insertSavedSite(savedSite: SavedSite): Long = tripDao.insertSavedSite(savedSite)

    suspend fun updateSavedSite(savedSite: SavedSite) {
        tripDao.updateSavedSite(savedSite)
    }

    suspend fun deleteSavedSite(id: Long) {
        tripDao.deleteSavedSite(id)
    }

    fun getSavedSitesForTrip(tripId: Long): LiveData<List<SavedSite>> =
        tripDao.getSavedSitesForTrip(tripId)

    fun getTripWithSites(tripId: Long): LiveData<TripWithSites> {
        return tripDao.getTripWithSites(tripId)
    }
}