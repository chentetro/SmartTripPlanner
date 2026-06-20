package com.example.smarttripplanner.data.repository

import androidx.lifecycle.LiveData
import com.example.smarttripplanner.data.local_db.TripDao
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.data.model.Trip
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val tripDao: TripDao
) {

    fun getAllTrips(): LiveData<List<Trip>> = tripDao.getAllTrips()

    fun getTripById(tripId: Long): LiveData<Trip> = tripDao.getTripById(tripId)

    fun getFavoriteTrips(): LiveData<List<Trip>> = tripDao.getFavoriteTrips()

    suspend fun insertTrip(trip: Trip): Long = tripDao.insertTrip(trip)

    suspend fun deleteTripById(tripId: Long) {
        tripDao.deleteTripById(tripId)
    }

    suspend fun updateTripDetails(
        tripId: Long,
        name: String,
        startTime: String,
        endTime: String
    ) {
        tripDao.updateTripDetails(
            tripId = tripId,
            name = name,
            startTime = startTime,
            endTime = endTime
        )
    }

    suspend fun updateFavoriteStatus(tripId: Long, isFavorite: Boolean) {
        tripDao.updateFavoriteStatus(tripId, isFavorite)
    }

    suspend fun deleteSavedSite(id: Long) {
        tripDao.deleteSavedSite(id)
    }

    fun getSavedSitesForTrip(tripId: Long): LiveData<List<SavedSite>> =
        tripDao.getSavedSitesForTrip(tripId)
}