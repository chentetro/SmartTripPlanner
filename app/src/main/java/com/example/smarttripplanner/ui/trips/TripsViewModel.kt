package com.example.smarttripplanner.ui.trips

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    val allTrips: LiveData<List<Trip>> = tripRepository.getAllTrips()

    fun deleteTrip(tripId: Long) {
        viewModelScope.launch {
            tripRepository.deleteTripById(tripId)
        }
    }
}
