package com.example.finalproject.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalproject.data.model.Site

@Dao
interface SiteDao {

    // שמירת אתר/מסעדה ב-Database (קורה ברגע שהם חוזרים מה-API של OpenTripMap)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSite(site: Site)

    // הצגת פרטי האתר/מסעדה לפי ה-ID הייחודי שלו (xid) באמצעות LiveData
    @Query("SELECT * FROM sites WHERE site_id = :siteId")
    fun getSiteDetails(siteId: String): LiveData<Site>
}