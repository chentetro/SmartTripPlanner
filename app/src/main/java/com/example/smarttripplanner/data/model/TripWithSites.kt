package com.example.finalproject.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * One site on a trip's itinerary: junction schedule + full [Site] details.
 */
data class SiteWithItineraryDetails(
    @Embedded
    val itineraryInfo: TripSiteCrossRef,

    @Relation(
        parentColumn = "site_id",
        entityColumn = "site_id",
        entity = Site::class
    )
    val site: Site
)

/**
 * A trip and all of its sites, each with per-trip schedule from [TripSiteCrossRef].
 */
data class TripWithSites(
    @Embedded
    val trip: Trip,

    @Relation(
        parentColumn = "trip_id",
        entityColumn = "trip_id",
        entity = TripSiteCrossRef::class
    )
    val sites: List<SiteWithItineraryDetails> = emptyList()
)