package com.example.finalproject.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalproject.data.model.Trip
import com.example.finalproject.data.model.TripSiteCrossRef
import com.example.finalproject.data.model.TripWithSites

@Dao
interface TripDao {

    // 1. הוספה של טיול (או עדכון אוטומטי אם יש התנגשות)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    // 2. מחיקה של טיול (ימחק אוטומטית גם את המסלול שלו בזכות ה-CASCADE)
    @Delete
    suspend fun deleteTrip(trip: Trip)

    // 3. עדכון של טיול (שם, תאריך, שעות - מעדכן את כל הישות לפי ה-ID שלה)
    @Update
    suspend fun updateTrip(trip: Trip)

    // 4 + 5. הוספה/מחיקה ממועדפים (שאילתה ממוקדת שמשנה רק את הביט הבוליאני)
    @Query("UPDATE trips SET is_favorite = :isFavorite WHERE trip_id = :tripId")
    suspend fun updateFavoriteStatus(tripId: Long, isFavorite: Boolean)

    // 6. פונקציה שמחזירה את כל הטיולים בזמן אמת באמצעות LiveData
    @Query("SELECT * FROM trips")
    fun getAllTrips(): LiveData<List<Trip>>

    // 7. פונקציה שמחזירה את כל הטיולים המועדפים בזמן אמת באמצעות LiveData
    @Query("SELECT * FROM trips WHERE is_favorite = 1")
    fun getFavoriteTrips(): LiveData<List<Trip>>

    @Query("SELECT * FROM trips WHERE trip_id = :tripId")
    fun getTripById(tripId: Long): LiveData<Trip>

    // הוספת אתר/מסעדה לתוך מסלול של טיול
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSiteToTrip(crossRef: TripSiteCrossRef)

    // מחיקה של אתר ספציפי מתוך טיול ספציפי
    @Query("DELETE FROM trip_site_cross_ref WHERE trip_id = :tripId AND site_id = :siteId")
    suspend fun removeSiteFromTrip(tripId: Long, siteId: String)

    // עדכון שעות ביקור בלבד עבור אתר בתוך טיול ספציפי
    @Query("UPDATE trip_site_cross_ref SET start_time = :startTime, end_time = :endTime WHERE trip_id = :tripId AND site_id = :siteId")
    suspend fun updateSiteHoursInTrip(tripId: Long, siteId: String, startTime: String, endTime: String)

    @Transaction
    @Query("SELECT * FROM trips WHERE trip_id = :tripId")
    fun getTripWithSites(tripId: Long): LiveData<TripWithSites>
}