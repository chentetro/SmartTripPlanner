package com.example.smarttripplanner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val imageUrl: String,
    val description: String
)