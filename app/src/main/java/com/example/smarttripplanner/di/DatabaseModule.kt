package com.example.smarttripplanner.di

import android.content.Context
import com.example.smarttripplanner.data.local_db.SavedSiteDao
import com.example.smarttripplanner.data.local_db.TripDao
import com.example.smarttripplanner.data.local_db.TripPlannerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTripPlannerDatabase(
        @ApplicationContext context: Context
    ): TripPlannerDatabase {
        return TripPlannerDatabase.getDatabase(context)
    }

    @Provides
    fun provideSavedSiteDao(database: TripPlannerDatabase): SavedSiteDao {
        return database.savedSiteDao()
    }

    @Provides
    fun provideTripDao(database: TripPlannerDatabase): TripDao {
        return database.tripDao()
    }
}
