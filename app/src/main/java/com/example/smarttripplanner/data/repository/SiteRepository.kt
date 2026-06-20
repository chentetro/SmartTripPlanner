package com.example.smarttripplanner.data.repository

import androidx.lifecycle.LiveData
import com.example.smarttripplanner.BuildConfig
import com.example.smarttripplanner.data.local_db.SavedSiteDao
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.data.remote.GoogleCircleDto
import com.example.smarttripplanner.data.remote.GoogleLatLngDto
import com.example.smarttripplanner.data.remote.GoogleLocationRestrictionDto
import com.example.smarttripplanner.data.remote.GoogleNearbySearchRequestDto
import com.example.smarttripplanner.data.remote.GooglePlaceDto
import com.example.smarttripplanner.data.remote.GooglePlacesApi
import javax.inject.Inject

class SiteRepository @Inject constructor(
    private val savedSiteDao: SavedSiteDao,
    private val googlePlacesApi: GooglePlacesApi
) {

    fun getSavedSiteDetails(siteId: Long): LiveData<SavedSite> =
        savedSiteDao.getSavedSiteDetails(siteId)

    fun getSavedSiteDetailsByPlaceId(placeId: String): LiveData<SavedSite> =
        savedSiteDao.getSavedSiteDetailsByPlaceId(placeId)

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

    suspend fun fetchAndSaveSitesForTrip(
        tripId: Long,
        lat: Double,
        lon: Double,
        radius: Int,
        kinds: String
    ) {
        val remoteSites = googlePlacesApi.searchNearby(
            apiKey = BuildConfig.GOOGLE_PLACES_API_KEY,
            fieldMask = NEARBY_SEARCH_FIELD_MASK,
            request = GoogleNearbySearchRequestDto(
                includedTypes = kinds.toGooglePlaceTypes(),
                maxResultCount = MAX_NEARBY_RESULTS,
                locationRestriction = GoogleLocationRestrictionDto(
                    circle = GoogleCircleDto(
                        center = GoogleLatLngDto(
                            latitude = lat,
                            longitude = lon
                        ),
                        radius = radius.toDouble()
                    )
                )
            )
        ).places.orEmpty()

        require(remoteSites.isNotEmpty()) {
            "No places found for the selected questionnaire filters."
        }

        val savedSites = remoteSites.mapIndexedNotNull { index, site ->
            val placeId = site.id?.takeIf { it.isNotBlank() } ?: return@mapIndexedNotNull null
            val location = site.location ?: return@mapIndexedNotNull null
            val category = site.toCategory()

            SavedSite(
                tripIdOfParent = tripId,
                placeId = placeId,
                name = site.displayName?.text.orEmpty().ifBlank { "Unnamed place" },
                category = category,
                latitude = location.latitude,
                longitude = location.longitude,
                rating = null,
                imageUrl = category.toFallbackDrawableName(),
                description = null,
                siteUrl = null,
                visitOrder = index + 1
            )
        }

        require(savedSites.isNotEmpty()) {
            "Google Places returned places without usable coordinates."
        }

        savedSites.forEach { savedSite ->
            savedSiteDao.insertSavedSite(savedSite)
        }

        savedSites.forEach { savedSite ->
            try {
                fetchAndSaveMissingDetails(savedSite.placeId)
            } catch (_: Exception) {
                // Keep the trip creation flow responsive even if one place details call fails.
            }
        }
    }

    suspend fun fetchAndSaveMissingDetails(placeId: String) {
        require(placeId.isNotBlank()) {
            "Missing Google place id."
        }

        val details = googlePlacesApi.getPlaceDetails(
            apiKey = BuildConfig.GOOGLE_PLACES_API_KEY,
            fieldMask = PLACE_DETAILS_FIELD_MASK,
            placeId = placeId
        )

        savedSiteDao.updateMissingDetails(
            placeId = placeId,
            rating = details.rating?.toString(),
            description = details.editorialSummary?.text,
            siteUrl = details.websiteUri
        )

        details.photos
            ?.firstOrNull()
            ?.name
            ?.toGooglePhotoUrl()
            ?.let { photoUrl ->
                savedSiteDao.updateSavedSiteImageUrl(
                    placeId = placeId,
                    imageUrl = photoUrl
                )
            }
    }

    private fun String.toGooglePlaceTypes(): List<String> {
        val types = split(",")
            .map { it.trim() }
            .flatMap { kind ->
                when (kind) {
                    "natural" -> listOf(
                        "park",
                        "national_park"
                    )
                    "museums" -> listOf("museum")
                    "foods" -> listOf(
                        "restaurant",
                        "cafe"
                    )
                    "historic" -> listOf(
                        "historical_landmark",
                        "tourist_attraction"
                    )
                    "architecture" -> listOf(
                        "tourist_attraction",
                        "historical_landmark"
                    )
                    "cultural" -> listOf(
                        "art_gallery",
                        "performing_arts_theater"
                    )
                    else -> emptyList()
                }
            }
            .distinct()

        return types.ifEmpty {
            listOf("tourist_attraction", "park")
        }
    }

    private fun GooglePlaceDto.toCategory(): String {
        val types = types.orEmpty()
        return when {
            primaryType == "museum" || "museum" in types -> "museum"
            primaryType == "restaurant" || primaryType == "cafe" ||
                "restaurant" in types || "cafe" in types -> "food"
            primaryType == "park" || primaryType == "national_park" ||
                "park" in types || "national_park" in types -> "nature"
            primaryType == "art_gallery" || primaryType == "performing_arts_theater" ||
                "art_gallery" in types || "performing_arts_theater" in types -> "culture"
            primaryType == "historical_landmark" || primaryType == "tourist_attraction" ||
                "historical_landmark" in types || "tourist_attraction" in types -> "historic"
            else -> "place"
        }
    }

    private fun String.toFallbackDrawableName(): String {
        return "ic_launcher_background"
    }

    private fun String.toGooglePhotoUrl(): String {
        return "$GOOGLE_PLACES_BASE_URL/v1/$this/media" +
            "?maxWidthPx=$PHOTO_MAX_WIDTH_PX&key=${BuildConfig.GOOGLE_PLACES_API_KEY}"
    }

    private companion object {
        const val GOOGLE_PLACES_BASE_URL = "https://places.googleapis.com"
        const val MAX_NEARBY_RESULTS = 7
        const val PHOTO_MAX_WIDTH_PX = 800
        const val NEARBY_SEARCH_FIELD_MASK =
            "places.id,places.displayName,places.primaryType,places.types,places.location"
        const val PLACE_DETAILS_FIELD_MASK =
            "id,rating,editorialSummary,photos,websiteUri"
    }
}
