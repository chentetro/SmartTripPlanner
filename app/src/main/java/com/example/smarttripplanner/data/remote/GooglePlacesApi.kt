package com.example.smarttripplanner.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GooglePlacesApi {

    @POST("v1/places:searchNearby")
    suspend fun searchNearby(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Body request: GoogleNearbySearchRequestDto
    ): GoogleNearbySearchResponseDto

    @GET("v1/places/{placeId}")
    suspend fun getPlaceDetails(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String,
        @Path("placeId") placeId: String
    ): GooglePlaceDto
}
