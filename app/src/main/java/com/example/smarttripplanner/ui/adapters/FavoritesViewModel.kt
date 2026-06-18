package com.example.smarttripplanner.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttripplanner.data.local.AppDatabase
import com.example.smarttripplanner.data.repository.TripRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = TripRepository(
        AppDatabase.getDatabase(app).favoriteDao()
    )

    val favorites = repository.getAllFavorites()

    fun removeFavorite(id: String) {
        viewModelScope.launch { repository.removeFavorite(id) }
    }
}