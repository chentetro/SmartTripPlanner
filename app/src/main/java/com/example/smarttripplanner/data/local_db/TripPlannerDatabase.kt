package com.example.finalproject.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finalproject.data.model.Trip
import com.example.finalproject.data.model.Site
import com.example.finalproject.data.model.TripSiteCrossRef

@Database(
    entities = [Trip::class, Site::class, TripSiteCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class TripPlannerDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun siteDao(): SiteDao

    companion object {
        @Volatile
        private var instance: TripPlannerDatabase? = null

        fun getDatabase(context: Context) = instance ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                TripPlannerDatabase::class.java,
                "trip_planner_db"
            )
                .allowMainThreadQueries().build()
        }
    }
}