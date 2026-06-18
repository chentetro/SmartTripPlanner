package com.example.smarttripplanner.data.repository

import com.example.smarttripplanner.data.local.FavoriteDao
import com.example.smarttripplanner.data.model.Destination
import com.example.smarttripplanner.data.model.FavoriteEntity
import com.example.smarttripplanner.data.remote.RetrofitClient

class TripRepository(private val favoriteDao: FavoriteDao) {

    // Popular cities to show on home screen
    private val popularSlugs = listOf(
        "paris" to "Paris, France",
        "london" to "London, UK",
        "new-york-city" to "New York, USA",
        "tokyo" to "Tokyo, Japan",
        "dubai" to "Dubai, UAE",
        "barcelona" to "Barcelona, Spain",
        "amsterdam" to "Amsterdam, Netherlands",
        "singapore" to "Singapore"
    )

    suspend fun getPopularDestinations(): List<Destination> {
        return popularSlugs.mapNotNull { (slug, location) ->
            try {
                // Endpoint 3 — fetch real photo from Teleport
                val imagesResponse = RetrofitClient.teleportApi.getCityImages(slug)
                val imageUrl = imagesResponse.photos.firstOrNull()?.image?.web ?: ""

                // Endpoint 2 — fetch real score
                val scoresResponse = RetrofitClient.teleportApi.getCityScores(slug)
                val score = (scoresResponse.score / 10).toFloat().coerceIn(0f, 10f)
                val description = scoresResponse.summary

                Destination(
                    id = slug,
                    name = location.substringBefore(","),
                    location = location.substringAfter(", "),
                    imageUrl = imageUrl,
                    rating = ((score * 5) / 10 + 4).coerceIn(3.5f, 5f),
                    temperature = 22,
                    price = 100 + (slug.length * 7),
                    description = description.take(200),
                    category = "City"
                )
            } catch (e: Exception) {
                // Fallback if API fails for this city
                Destination(
                    id = slug,
                    name = location.substringBefore(","),
                    location = location.substringAfter(", "),
                    imageUrl = "",
                    description = "A beautiful destination worth exploring."
                )
            }
        }
    }

    suspend fun searchDestinations(query: String): List<Destination> {
        return try {
            // Endpoint 1 — search all urban areas by name
            val response = RetrofitClient.teleportApi.getUrbanAreas()
            response.links.items
                .filter { it.name.contains(query, ignoreCase = true) }
                .take(10)
                .map { item ->
                    val slug = item.href.substringAfter("slug:").removeSuffix("/")
                    Destination(
                        id = slug,
                        name = item.name,
                        location = "World",
                        imageUrl = ""
                    )
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Room operations
    fun getAllFavorites() = favoriteDao.getAllFavorites()
    fun isFavorite(id: String) = favoriteDao.isFavorite(id)
    suspend fun addFavorite(fav: FavoriteEntity) = favoriteDao.insert(fav)
    suspend fun removeFavorite(id: String) = favoriteDao.deleteById(id)
}