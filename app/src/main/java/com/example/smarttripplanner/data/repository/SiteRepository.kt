package com.example.smarttripplanner.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.smarttripplanner.data.local_db.SavedSiteDao
import com.example.smarttripplanner.data.local_db.TripPlannerDatabase
import com.example.smarttripplanner.data.model.SavedSite

class SiteRepository(application: Application) {

    private val savedSiteDao: SavedSiteDao

    init {
        val db = TripPlannerDatabase.getDatabase(application.applicationContext)
        savedSiteDao = db.savedSiteDao()
    }

    fun getSavedSiteDetails(siteId: Long): LiveData<SavedSite> =
        savedSiteDao.getSavedSiteDetails(siteId)

    fun getSavedSitesForTrip(tripId: Long): LiveData<List<SavedSite>> =
        savedSiteDao.getSavedSitesForTrip(tripId)

    suspend fun insertSavedSite(savedSite: SavedSite): Long =
        savedSiteDao.insertSavedSite(savedSite)

    suspend fun updateSavedSite(savedSite: SavedSite) {
        savedSiteDao.updateSavedSite(savedSite)
    }

    suspend fun deleteSavedSite(siteId: Long) {
        savedSiteDao.deleteSavedSite(siteId)
    }

    // --- עבודה מול ה-API החיצוני (OpenTripMap) ---

    fun fetchSitesFromApi(lat: Double, lon: Double, radius: Int, kinds: String) {
        // כאן ייכתב הקוד של ה-API שיביא את האתרים והמסעדות בישראל
    }
}
