package com.example.smarttripplanner.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

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

    @Streaming
    @GET("v1/{photoName}/media")
    suspend fun fetchPlacePhoto(
        @Header("X-Goog-Api-Key") apiKey: String,
        @Path("photoName", encoded = true) photoName: String,
        @Query("maxWidthPx") maxWidthPx: Int? = null,
        @Query("maxHeightPx") maxHeightPx: Int? = null
    ): Response<ResponseBody>
}
