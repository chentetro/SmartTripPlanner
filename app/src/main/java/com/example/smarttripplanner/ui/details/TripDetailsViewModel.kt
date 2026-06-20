package com.example.smarttripplanner.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TripDetailsEditState {
    data object Idle : TripDetailsEditState()
    data object Saved : TripDetailsEditState()
    data class Error(val message: String) : TripDetailsEditState()
}

@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val currentTripId = MutableLiveData<Long>()
    private val _editState = MutableLiveData<TripDetailsEditState>(TripDetailsEditState.Idle)

    val trip: LiveData<Trip> = currentTripId.switchMap { tripId ->
        tripRepository.getTripById(tripId)
    }

    val savedSites: LiveData<List<SavedSite>> = currentTripId.switchMap { tripId ->
        tripRepository.getSavedSitesForTrip(tripId)
    }

    val editState: LiveData<TripDetailsEditState> = _editState

    fun setTripId(tripId: Long) {
        if (currentTripId.value == tripId) return
        currentTripId.value = tripId
    }

    fun deleteSite(siteId: Long) {
        viewModelScope.launch {
            tripRepository.deleteSavedSite(siteId)
        }
    }

    fun toggleFavoriteStatus(tripId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            tripRepository.updateFavoriteStatus(tripId, !currentStatus)
        }
    }

    fun updateTripDetails(
        tripId: Long,
        name: String,
        startTime: String,
        endTime: String
    ) {
        val cleanName = name.trim()
        val cleanStartTime = startTime.trim()
        val cleanEndTime = endTime.trim()

        if (tripId == INVALID_TRIP_ID) {
            _editState.value = TripDetailsEditState.Error("Trip not found.")
            return
        }

        if (cleanName.isBlank()) {
            _editState.value = TripDetailsEditState.Error("Please enter a trip name.")
            return
        }

        if (cleanStartTime.isBlank() || cleanEndTime.isBlank()) {
            _editState.value = TripDetailsEditState.Error("Please enter start and end times.")
            return
        }

        viewModelScope.launch {
            runCatching {
                tripRepository.updateTripDetails(
                    tripId = tripId,
                    name = cleanName,
                    startTime = cleanStartTime,
                    endTime = cleanEndTime
                )
            }.onSuccess {
                _editState.value = TripDetailsEditState.Saved
            }.onFailure {
                _editState.value = TripDetailsEditState.Error("Could not update trip details.")
            }
        }
    }

    fun resetEditState() {
        _editState.value = TripDetailsEditState.Idle
    }

    private companion object {
        const val INVALID_TRIP_ID = -1L
    }
}
