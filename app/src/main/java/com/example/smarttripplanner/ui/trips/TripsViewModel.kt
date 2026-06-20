package com.example.smarttripplanner.ui.trips

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(
    tripRepository: TripRepository
) : ViewModel() {

    val allTrips: LiveData<List<Trip>> = tripRepository.getAllTrips()
}
