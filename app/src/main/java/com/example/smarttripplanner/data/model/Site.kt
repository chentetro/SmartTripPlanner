package com.example.finalproject.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "sites")
data class Site(
    @PrimaryKey
    @ColumnInfo(name = "site_id")
    val siteId: String, // מפתח ראשי קבוע שמגיע מה-API (xid) - ללא autoGenerate

    @ColumnInfo(name = "name_site")
    val name: String,

    @ColumnInfo(name = "category_site")
    val category: String, // מכיל את ה-kinds (למשל: "nature,forests")

    @ColumnInfo(name = "latitude_site")
    val latitude: Double,

    @ColumnInfo(name = "longitude_site")
    val longitude: Double,

    @ColumnInfo(name = "rating_site")
    val rating: Int, // מדד הפופולריות של האתר

    @ColumnInfo(name = "image_url")
    val imageUrl: String?, // סימן השאלה אומר שהשדה יכול לקבל Null אם אין תמונה באתר

    @ColumnInfo(name = "description_site")
    val description: String?, // תקציר התיאור (Nullable)

    @ColumnInfo(name = "site_url")
    val siteUrl: String? // קישור לוויקיפדיה או מידע חיצוני (Nullable)
) : Parcelable