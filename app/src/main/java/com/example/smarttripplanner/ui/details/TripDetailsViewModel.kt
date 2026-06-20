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

@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val currentTripId = MutableLiveData<Long>()

    val trip: LiveData<Trip> = currentTripId.switchMap { tripId ->
        tripRepository.getTripById(tripId)
    }

    val savedSites: LiveData<List<SavedSite>> = currentTripId.switchMap { tripId ->
        tripRepository.getSavedSitesForTrip(tripId)
    }

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
}
