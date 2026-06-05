package com.example.finalproject.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.finalproject.data.local_db.SiteDao
import com.example.finalproject.data.local_db.TripPlannerDatabase
import com.example.finalproject.data.model.Site


class SiteRepository(application: Application) {

    private val siteDao: SiteDao

    init {
        val db = TripPlannerDatabase.getDatabase(application.applicationContext)
        siteDao = db.siteDao()
    }

    // שליפת פרטי אתר שמור מה-Database המקומי בזמן אמת
    fun getSiteDetails(siteId: String): LiveData<Site> = siteDao.getSiteDetails(siteId)

    // שמירת אתר ב-Database המקומי (קורה כשהמשתמש בוחר אתר שחזר מה-API)
    // הוספת suspend מאפשרת לקרוא לפונקציית ה-DAO שהיא גם suspend
    suspend fun insertSite(site: Site) {
        siteDao.insertSite(site)
    }



    // --- עבודה מול ה-API החיצוני (OpenTripMap) ---

    // פונקציה זו תתממש בהמשך ותחזיר רשימת אתרים מהאינטרנט לפי פרמטרים של מיקום ורדיוס
    fun fetchSitesFromApi(lat: Double, lon: Double, radius: Int, kinds: String) {
        // כאן ייכתב הקוד של ה-API שיביא את האתרים והמסעדות בישראל
        // ברגע שהם יחזרו מהרשת, נציג אותם למשתמש, ואם הוא יבחר אחד - נשמור אותו באמצעות insertSite
    }
}
