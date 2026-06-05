package com.example.finalproject.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.finalproject.data.local_db.TripDao
import com.example.finalproject.data.local_db.TripPlannerDatabase
import com.example.finalproject.data.model.Trip
import com.example.finalproject.data.model.TripSiteCrossRef
import com.example.finalproject.data.model.TripWithSites

class TripRepository(application: Application) {

    // Making this non-nullable fixes the LiveData return type issues
    private val tripDao: TripDao

    init {
        // שליפת מופע ה-Database וה-Dao בצורה בטוחה
        val db = TripPlannerDatabase.getDatabase(application.applicationContext)
        tripDao = db.tripDao()
    }

    // שליפת כל הטיולים בזמן אמת
    fun getAllTrips(): LiveData<List<Trip>> = tripDao.getAllTrips()

    // שליפת טיול ספציפי לפי ID
    fun getTripById(tripId: Long): LiveData<Trip> = tripDao.getTripById(tripId)

    // שליפת כל הטיולים המועדפים
    fun getFavoriteTrips(): LiveData<List<Trip>> = tripDao.getFavoriteTrips()

    // הוספת טיול חדש - חייב להיות suspend
    suspend fun insertTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    // מחיקת טיול - חייב להיות suspend
    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    // עדכון פרטי טיול (שם, תאריך, שעות) - חייב להיות suspend
    suspend fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip)
    }

    // עדכון סטטוס מועדף - חייב להיות suspend
    suspend fun updateFavoriteStatus(tripId: Long, isFavorite: Boolean) {
        tripDao.updateFavoriteStatus(tripId, isFavorite)
    }


    // --- ניהול הלו"ז (טבלת הקשר) ---

    // הוספת אתר/מסעדה לתוך מסלול הטיול - חייב להיות suspend
    suspend fun insertSiteToTrip(crossRef: TripSiteCrossRef) {
        tripDao.insertSiteToTrip(crossRef)
    }

    // מחיקת אתר מתוך טיול ספציפי - חייב להיות suspend
    suspend fun removeSiteFromTrip(tripId: Long, siteId: String) {
        tripDao.removeSiteFromTrip(tripId, siteId)
    }

    fun getTripWithSites(tripId: Long): LiveData<TripWithSites> {
        return tripDao.getTripWithSites(tripId)
    }
}