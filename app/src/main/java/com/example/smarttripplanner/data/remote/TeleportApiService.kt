package com.example.smarttripplanner.data.remote

import com.example.smarttripplanner.data.remote.dto.CityImagesResponse
import com.example.smarttripplanner.data.remote.dto.CityScoresResponse
import com.example.smarttripplanner.data.remote.dto.UrbanAreasResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface TeleportApiService {

    // Endpoint 1 — list of all world cities
    @GET("urban_areas/")
    suspend fun getUrbanAreas(): UrbanAreasResponse

    // Endpoint 2 — quality scores for a specific city
    @GET("urban_areas/slug:{slug}/scores/")
    suspend fun getCityScores(@Path("slug") slug: String): CityScoresResponse

    // Endpoint 3 — photos for a specific city
    @GET("urban_areas/slug:{slug}/images/")
    suspend fun getCityImages(@Path("slug") slug: String): CityImagesResponse
}