package com.example.smarttripplanner.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttripplanner.data.model.SavedSite
import com.example.smarttripplanner.data.repository.SiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SiteDetailsViewModel @Inject constructor(
    private val siteRepository: SiteRepository
) : ViewModel() {

    private val _currentPlaceId = MutableLiveData<String>()
    private val _site = MediatorLiveData<SavedSite>()
    private var siteSource: LiveData<SavedSite>? = null

    val site: LiveData<SavedSite> = _site

    fun setPlaceId(placeId: String) {
        if (placeId.isBlank() || _currentPlaceId.value == placeId) return
        _currentPlaceId.value = placeId
        observeSite(placeId)
    }

    fun refreshMissingDetails(placeId: String) {
        if (placeId.isBlank()) return

        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    siteRepository.fetchAndSaveMissingDetails(placeId)
                }
            }
        }
    }

    private fun observeSite(placeId: String) {
        siteSource?.let { _site.removeSource(it) }
        siteSource = siteRepository.getSavedSiteDetailsByPlaceId(placeId).also { source ->
            _site.addSource(source) { site ->
                _site.value = site
            }
        }
    }
}
