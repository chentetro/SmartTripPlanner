package com.example.smarttripplanner.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smarttripplanner.data.local.AppDatabase
import com.example.smarttripplanner.data.model.Destination
import com.example.smarttripplanner.data.repository.TripRepository
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = TripRepository(
        AppDatabase.getDatabase(app).favoriteDao()
    )

    val popularDestinations     = MutableLiveData<List<Destination>>()
    val recommendedDestinations = MutableLiveData<List<Destination>>()
    val isLoading               = MutableLiveData<Boolean>()

    fun loadDestinations() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val all = repository.getPopularDestinations()
                popularDestinations.value     = all.take(5)
                recommendedDestinations.value = all.drop(2)
            } catch (e: Exception) {
                popularDestinations.value     = emptyList()
                recommendedDestinations.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }
}