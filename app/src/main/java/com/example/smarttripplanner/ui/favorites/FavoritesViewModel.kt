package com.example.smarttripplanner.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    tripRepository: TripRepository
) : ViewModel() {

    val favoriteTrips: LiveData<List<Trip>> = tripRepository.getFavoriteTrips()
}
