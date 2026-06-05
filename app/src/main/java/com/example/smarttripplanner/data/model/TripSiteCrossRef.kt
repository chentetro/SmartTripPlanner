package com.example.finalproject.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "trip_site_cross_ref",
    primaryKeys = ["trip_id", "site_id"],
    indices = [Index("trip_id"), Index("site_id")],
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["trip_id"],
            childColumns = ["trip_id"],
            onDelete = ForeignKey.CASCADE // אם טיול נמחק, כל השורות שלו במסלול נמחקות אוטומטית!
        ),
        ForeignKey(
            entity = Site::class,
            parentColumns = ["site_id"],
            childColumns = ["site_id"],
            onDelete = ForeignKey.CASCADE // אם אתר נמחק מהמערכת, הוא מוסר אוטומטית מהמסלול
        )
    ]
)
data class TripSiteCrossRef(
    @ColumnInfo(name = "trip_id")
    val tripId: Long, // תואם ל-ID של הטיול

    @ColumnInfo(name = "site_id")
    val siteId: String, // תואם ל-xid מה-API של האתר

    @ColumnInfo(name = "visit_order")
    val visitOrder: Int,

    @ColumnInfo(name = "start_time")
    val startTime: String,

    @ColumnInfo(name = "end_time")
    val endTime: String,

    @ColumnInfo(name = "travel_time_from_previous")
    val travelTimeFromPrevious: Int
) : Parcelable