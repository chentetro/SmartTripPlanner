package com.example.smarttripplanner.data.model

data class Destination(
    val id: String,
    val name: String,
    val location: String,
    val imageUrl: String,
    val rating: Float = 4.8f,
    val temperature: Int = 24,
    val price: Int = 120,
    val description: String = "",
    val category: String = ""
)