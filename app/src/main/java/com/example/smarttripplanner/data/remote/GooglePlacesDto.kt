package com.example.smarttripplanner.data.remote

import com.google.gson.annotations.SerializedName

data class GoogleNearbySearchRequestDto(
    @SerializedName("includedTypes")
    val includedTypes: List<String>,
    @SerializedName("maxResultCount")
    val maxResultCount: Int,
    @SerializedName("locationRestriction")
    val locationRestriction: GoogleLocationRestrictionDto
)

data class GoogleLocationRestrictionDto(
    @SerializedName("circle")
    val circle: GoogleCircleDto
)

data class GoogleCircleDto(
    @SerializedName("center")
    val center: GoogleLatLngDto,
    @SerializedName("radius")
    val radius: Double
)

data class GoogleLatLngDto(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)

data class GoogleNearbySearchResponseDto(
    @SerializedName("places")
    val places: List<GooglePlaceDto>?
)

data class GooglePlaceDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("displayName")
    val displayName: GoogleLocalizedTextDto?,
    @SerializedName("primaryType")
    val primaryType: String?,
    @SerializedName("types")
    val types: List<String>?,
    @SerializedName("location")
    val location: GoogleLatLngDto?,
    @SerializedName("rating")
    val rating: Double?,
    @SerializedName("editorialSummary")
    val editorialSummary: GoogleLocalizedTextDto?,
    @SerializedName("photos")
    val photos: List<GooglePhotoDto>?,
    @SerializedName("websiteUri")
    val websiteUri: String?
)

data class GoogleLocalizedTextDto(
    @SerializedName("text")
    val text: String?
)

data class GooglePhotoDto(
    @SerializedName("name")
    val name: String?
)
