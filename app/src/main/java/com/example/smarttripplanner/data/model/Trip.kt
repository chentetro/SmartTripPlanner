package com.example.finalproject.data.model



import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "trip_id")
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String, // קריטי! הוספנו כדי לקשר ל-Firebase Auth שסגרנו עליו בארכיטקטורה

    @ColumnInfo(name = "trip_name")
    val tripName: String,

    @ColumnInfo(name = "trip_date")
    val tripDate: Long,

    @ColumnInfo(name = "total_start_time")
    val totalStartTime: String,

    @ColumnInfo(name = "total_end_time")
    val totalEndTime: String,

    @ColumnInfo(name = "participants_count")
    val participantsCount: Int,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,

    @ColumnInfo(name = "start_lat")
    val startLat: Double,

    @ColumnInfo(name = "start_lon")
    val startLon: Double,

    @ColumnInfo(name = "vibe")
    val vibe: String, // בשלב הבא נלמד איך להפוך את זה ל-Enum מוגבל לבחירה

    @ColumnInfo(name = "budget")
    val budget: Double, // שיניתי מ-String ל-Double כדי שתוכלי לעשות חישובים וסינונים מתמטיים באפליקציה!

    @ColumnInfo(name = "max_distance")
    val maxDistance: Double // שיניתי ל-Double כדי שתוכלי לחשב רדיוס מרחק אמיתי
) : Parcelable