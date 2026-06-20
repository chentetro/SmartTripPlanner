package com.example.smarttripplanner.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.data.model.Trip

@Database(
    entities = [Trip::class, SavedSite::class],
    version = 5,
    exportSchema = false
)
abstract class TripPlannerDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun savedSiteDao(): SavedSiteDao

    companion object {
        @Volatile
        private var instance: TripPlannerDatabase? = null

        fun getDatabase(context: Context) = instance ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                TripPlannerDatabase::class.java,
                "trip_planner_db"
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
                .also { instance = it }
        }
    }
}