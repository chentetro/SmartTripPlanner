package com.example.smarttripplanner.data.remote.dto

import com.google.gson.annotations.SerializedName

// ---- Urban Areas List ----
data class UrbanAreasResponse(
    @SerializedName("_links") val links: UrbanAreasLinks
)
data class UrbanAreasLinks(
    @SerializedName("ua:item") val items: List<UrbanAreaItem>
)
data class UrbanAreaItem(val href: String, val name: String)

// ---- City Images ----
data class CityImagesResponse(val photos: List<TeleportPhoto>)
data class TeleportPhoto(val image: TeleportImage)
data class TeleportImage(val mobile: String, val web: String)

// ---- City Scores ----
data class CityScoresResponse(
    val summary: String,
    @SerializedName("teleport_city_score") val score: Double,
    val categories: List<ScoreCategory>
)
data class ScoreCategory(
    val name: String,
    @SerializedName("score_out_of_10") val score: Double
)