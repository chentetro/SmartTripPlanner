package com.example.smarttripplanner.ui.details

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class TripDetailsEditState {
    data object Idle : TripDetailsEditState()
    data object Saved : TripDetailsEditState()
    data class Error(val message: String) : TripDetailsEditState()
}

@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _currentTripId = MutableLiveData<Long>()
    private val _trip = MediatorLiveData<Trip>()
    private val _savedSites = MediatorLiveData<List<SavedSite>>()
    private val _editState = MutableLiveData<TripDetailsEditState>(TripDetailsEditState.Idle)
    private var tripSource: LiveData<Trip>? = null
    private var savedSitesSource: LiveData<List<SavedSite>>? = null

    val trip: LiveData<Trip> = _trip
    val savedSites: LiveData<List<SavedSite>> = _savedSites
    val editState: LiveData<TripDetailsEditState> = _editState

    fun setTripId(tripId: Long) {
        if (_currentTripId.value == tripId) return
        _currentTripId.value = tripId
        observeTrip(tripId)
        observeSavedSites(tripId)
    }

    fun deleteSite(siteId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripRepository.deleteSavedSite(siteId)
            }
        }
    }

    fun toggleFavoriteStatus(tripId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripRepository.updateFavoriteStatus(tripId, !currentStatus)
            }
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
            _editState.value = TripDetailsEditState.Error(context.getString(R.string.trip_not_found))
            return
        }

        if (cleanName.isBlank()) {
            _editState.value = TripDetailsEditState.Error(
                context.getString(R.string.questionnaire_enter_trip_name)
            )
            return
        }

        if (cleanStartTime.isBlank() || cleanEndTime.isBlank()) {
            _editState.value = TripDetailsEditState.Error(
                context.getString(R.string.trip_enter_start_end_times)
            )
            return
        }

        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    tripRepository.updateTripDetails(
                        tripId = tripId,
                        name = cleanName,
                        startTime = cleanStartTime,
                        endTime = cleanEndTime
                    )
                }
            }.onSuccess {
                _editState.value = TripDetailsEditState.Saved
            }.onFailure {
                _editState.value = TripDetailsEditState.Error(
                    context.getString(R.string.trip_update_details_error)
                )
            }
        }
    }

    fun resetEditState() {
        _editState.value = TripDetailsEditState.Idle
    }

    private fun observeTrip(tripId: Long) {
        tripSource?.let { _trip.removeSource(it) }
        tripSource = tripRepository.getTripById(tripId).also { source ->
            _trip.addSource(source) { trip ->
                _trip.value = trip
            }
        }
    }

    private fun observeSavedSites(tripId: Long) {
        savedSitesSource?.let { _savedSites.removeSource(it) }
        savedSitesSource = tripRepository.getSavedSitesForTrip(tripId).also { source ->
            _savedSites.addSource(source) { savedSites ->
                _savedSites.value = savedSites
            }
        }
    }

    private companion object {
        const val INVALID_TRIP_ID = -1L
    }
}
