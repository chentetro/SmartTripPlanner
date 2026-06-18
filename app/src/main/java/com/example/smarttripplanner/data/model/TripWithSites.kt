package com.example.smarttripplanner.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * A trip and all places saved directly under it.
 */
data class TripWithSites(
    @Embedded
    val trip: Trip,

    @Relation(
        parentColumn = "trip_id",
        entityColumn = "tripIdOfParent"
    )
    val sites: List<SavedSite> = emptyList()
)