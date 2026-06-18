package com.example.smarttripplanner.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "SavedSite",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["trip_id"],
            childColumns = ["tripIdOfParent"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripIdOfParent")]
)
data class SavedSite(
    @PrimaryKey(autoGenerate = true)
    val siteId: Long = 0,

    val tripIdOfParent: Long,

    val openTripMapXid: String,

    @ColumnInfo(name = "name_site")
    val name: String,

    @ColumnInfo(name = "category_site")
    val category: String,

    @ColumnInfo(name = "latitude_site")
    val latitude: Double,

    @ColumnInfo(name = "longitude_site")
    val longitude: Double,

    @ColumnInfo(name = "rating_site")
    val rating: String?,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    @ColumnInfo(name = "description_site")
    val description: String?,

    @ColumnInfo(name = "site_url")
    val siteUrl: String?,

    val visitOrder: Int? = null

) : Parcelable
