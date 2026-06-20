package com.example.smarttripplanner.ui.questionnaire

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.data.repository.SiteRepository
import com.example.smarttripplanner.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val siteRepository: SiteRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<QuestionnaireUiState>(QuestionnaireUiState.Idle)
    val uiState: LiveData<QuestionnaireUiState> = _uiState

    fun generateTrip(
        name: String,
        date: String,
        lat: Double,
        lon: Double,
        radiusKilometers: Int,
        selectedKindChipIds: List<Int>,
        participantsCount: Int = 1,
        startTime: String = "",
        endTime: String = ""
    ) {
        viewModelScope.launch {
            _uiState.value = QuestionnaireUiState.Loading

            try {
                val tripId = withContext(Dispatchers.IO) {
                    val kinds = selectedKindChipIds.toGooglePlaceKinds()
                    val radiusMeters = radiusKilometers.toMeters()
                    val trip = Trip(
                        userId = LOCAL_USER_ID,
                        tripName = name,
                        tripDate = parseTripDate(date),
                        totalStartTime = startTime,
                        totalEndTime = endTime,
                        participantsCount = participantsCount,
                        isFavorite = false,
                        startLat = lat,
                        startLon = lon,
                        vibe = kinds,
                        maxDistance = radiusKilometers.toDouble()
                    )

                    val createdTripId = tripRepository.insertTrip(trip)
                    siteRepository.fetchAndSaveSitesForTrip(
                        tripId = createdTripId,
                        lat = lat,
                        lon = lon,
                        radius = radiusMeters,
                        kinds = kinds
                    )
                    createdTripId
                }

                _uiState.value = QuestionnaireUiState.Success(tripId)
            } catch (exception: Exception) {
                _uiState.value = QuestionnaireUiState.Error(
                    exception.message ?: "Could not create trip. Please try again."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = QuestionnaireUiState.Idle
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
                ?: throw IllegalArgumentException("Please enter a valid trip date.")
        } catch (exception: ParseException) {
            throw IllegalArgumentException("Please enter the date as dd/mm/yyyy.")
        }
    }

    private companion object {
        const val LOCAL_USER_ID = "local_user"
        const val METERS_IN_KILOMETER = 1000
        const val DEFAULT_GOOGLE_PLACE_KINDS = "interesting_places"
    }
}
