package com.example.smarttripplanner.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.data.repository.SiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SiteDetailsViewModel @Inject constructor(
    private val siteRepository: SiteRepository
) : ViewModel() {

    private val currentPlaceId = MutableLiveData<String>()

    val site: LiveData<SavedSite> = currentPlaceId.switchMap { placeId ->
        siteRepository.getSavedSiteDetailsByPlaceId(placeId)
    }

    fun setPlaceId(placeId: String) {
        if (placeId.isBlank() || currentPlaceId.value == placeId) return
        currentPlaceId.value = placeId
    }

    fun refreshMissingDetails(placeId: String) {
        if (placeId.isBlank()) return

        viewModelScope.launch {
            runCatching {
                siteRepository.fetchAndSaveMissingDetails(placeId)
            }
        }
    }
}
