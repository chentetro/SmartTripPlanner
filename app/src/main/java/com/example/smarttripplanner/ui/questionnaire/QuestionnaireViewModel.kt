package com.example.smarttripplanner.ui.questionnaire

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.data.repository.SiteRepository
import com.example.smarttripplanner.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

sealed class QuestionnaireUiState {
    data object Idle : QuestionnaireUiState()
    data object Loading : QuestionnaireUiState()
    data class Success(val tripId: Long) : QuestionnaireUiState()
    data class Error(val message: String) : QuestionnaireUiState()
}

@HiltViewModel
class QuestionnaireViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val siteRepository: SiteRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableLiveData<QuestionnaireUiState>(QuestionnaireUiState.Idle)
    val uiState: LiveData<QuestionnaireUiState> = _uiState
    private var selectedLocation: SelectedLocation? = null

    fun updateLocation(latitude: Double, longitude: Double) {
        selectedLocation = SelectedLocation(
            latitude = latitude,
            longitude = longitude
        )
    }

    fun generateTrip(
        name: String,
        date: String,
        participantsText: String,
        startTime: String,
        endTime: String,
        radiusKilometers: Int,
        selectedKindChipIds: List<Int>
    ) {
        viewModelScope.launch {
            try {
                val location = requireSelectedLocation()
                val tripName = validateTripName(name)
                val tripDate = validateTripDate(date)
                val participantsCount = validateParticipants(participantsText)
                val cleanStartTime = startTime.trim()
                val cleanEndTime = endTime.trim()
                val kinds = selectedKindChipIds.toGooglePlaceKinds()
                val radiusMeters = radiusKilometers.toMeters()

                _uiState.value = QuestionnaireUiState.Loading

                val tripId = withContext(Dispatchers.IO) {
                    val trip = Trip(
                        userId = LOCAL_USER_ID,
                        tripName = tripName,
                        tripDate = tripDate,
                        totalStartTime = cleanStartTime,
                        totalEndTime = cleanEndTime,
                        participantsCount = participantsCount,
                        isFavorite = false,
                        startLat = location.latitude,
                        startLon = location.longitude,
                        vibe = kinds,
                        maxDistance = radiusKilometers.toDouble()
                    )

                    val createdTripId = tripRepository.insertTrip(trip)
                    siteRepository.fetchAndSaveSitesForTrip(
                        tripId = createdTripId,
                        lat = location.latitude,
                        lon = location.longitude,
                        radius = radiusMeters,
                        kinds = kinds
                    )
                    createdTripId
                }

                _uiState.value = QuestionnaireUiState.Success(tripId)
            } catch (exception: Exception) {
                _uiState.value = QuestionnaireUiState.Error(
                    exception.message ?: context.getString(R.string.questionnaire_create_trip_error)
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = QuestionnaireUiState.Idle
    }

    private fun requireSelectedLocation(): SelectedLocation {
        return selectedLocation
            ?: throw IllegalArgumentException(
                context.getString(R.string.questionnaire_secure_location_first)
            )
    }

    private fun validateTripName(name: String): String {
        return name.trim().ifBlank {
            throw IllegalArgumentException(context.getString(R.string.questionnaire_enter_trip_name))
        }
    }

    private fun validateTripDate(date: String): Long {
        val cleanDate = date.trim()
        if (cleanDate.isBlank()) {
            throw IllegalArgumentException(context.getString(R.string.questionnaire_enter_trip_date))
        }

        return parseTripDate(cleanDate)
    }

    private fun validateParticipants(participantsText: String): Int {
        val participantsCount = participantsText.trim().toIntOrNull()
        if (participantsCount == null || participantsCount <= 0) {
            throw IllegalArgumentException(
                context.getString(R.string.questionnaire_enter_valid_participants)
            )
        }

        return participantsCount
    }

    private fun List<Int>.toGooglePlaceKinds(): String {
        val selectedKinds = mapNotNull { chipId ->
            when (chipId) {
                R.id.chipNature -> "natural"
                R.id.chipMuseums -> "museums"
                R.id.chipFood -> "foods"
                R.id.chipHistory -> "historic"
                R.id.chipArchitecture -> "architecture"
                R.id.chipCulture -> "cultural"
                else -> null
            }
        }

        return selectedKinds.joinToString(",").ifBlank { DEFAULT_GOOGLE_PLACE_KINDS }
    }

    private fun Int.toMeters(): Int = this * METERS_IN_KILOMETER

    private fun parseTripDate(date: String): Long {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            isLenient = false
        }

        return try {
            formatter.parse(date)?.time
                ?: throw IllegalArgumentException(
                    context.getString(R.string.questionnaire_enter_valid_trip_date)
                )
        } catch (exception: ParseException) {
            throw IllegalArgumentException(context.getString(R.string.questionnaire_enter_date_format))
        }
    }

    private data class SelectedLocation(
        val latitude: Double,
        val longitude: Double
    )

    private companion object {
        const val LOCAL_USER_ID = "local_user"
        const val METERS_IN_KILOMETER = 1000
        const val DEFAULT_GOOGLE_PLACE_KINDS = "interesting_places"
    }
}
