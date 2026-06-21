package com.example.smarttripplanner.ui.trips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _allTrips = MediatorLiveData<List<Trip>>().apply {
        addSource(tripRepository.getAllTrips()) { trips ->
            value = trips
        }
    }
    val allTrips: LiveData<List<Trip>> = _allTrips

    fun deleteTrip(tripId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripRepository.deleteTripById(tripId)
            }
        }
    }
}
