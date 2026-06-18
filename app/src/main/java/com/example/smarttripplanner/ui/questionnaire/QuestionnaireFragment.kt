package com.example.smarttripplanner.ui.questionnaire

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.example.smarttripplanner.R
import com.example.smarttripplanner.databinding.QuestionnaireLayoutBinding

class QuestionnaireFragment : Fragment() {

    private var _binding: QuestionnaireLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLatitude: Double? = null
    private var userLongitude: Double? = null

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                fetchCurrentLocation()
            } else {
                binding.tvLocationStatus.text = "Location permission denied"
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = QuestionnaireLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        updateRadiusLabel(binding.sliderMaxDistance.value.toInt())
        binding.sliderMaxDistance.addOnChangeListener { _, value, _ ->
            updateRadiusLabel(value.toInt())
        }

        binding.btnGetMyLocation.setOnClickListener {
            requestLocationOrFetch()
        }

        binding.btnFindPlaces.setOnClickListener {
            showOpenTripMapQueryPreview()
        }
    }

    private fun updateRadiusLabel(distanceKm: Int) {
        binding.tvRadiusValue.text = "Max distance: $distanceKm km"
    }

    private fun requestLocationOrFetch() {
        if (hasFineLocationPermission()) {
            fetchCurrentLocation()
        } else {
            binding.tvLocationStatus.text = "Waiting for location permission"
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun hasFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        if (!hasFineLocationPermission()) {
            requestLocationOrFetch()
            return
        }

        binding.tvLocationStatus.text = "Getting current location..."
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location == null) {
                    binding.tvLocationStatus.text = "Could not secure location. Try again."
                    return@addOnSuccessListener
                }

                userLatitude = location.latitude
                userLongitude = location.longitude
                binding.tvLocationStatus.text = "Location secured successfully"
            }
            .addOnFailureListener {
                binding.tvLocationStatus.text = "Could not secure location. Try again."
            }
    }

    private fun showOpenTripMapQueryPreview() {
        val lat = userLatitude
        val lon = userLongitude

        if (lat == null || lon == null) {
            Toast.makeText(requireContext(), "Please secure your current location first", Toast.LENGTH_SHORT).show()
            return
        }

        val kinds = selectedOpenTripMapKinds()
        val radiusMeters = binding.sliderMaxDistance.value.toInt() * 1000
        val rate = selectedOpenTripMapRate()
        val rateText = rate?.let { ", rate=$it" } ?: ""

        Toast.makeText(
            requireContext(),
            "OpenTripMap: lat=$lat, lon=$lon, radius=$radiusMeters, kinds=$kinds$rateText",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun selectedOpenTripMapKinds(): String {
        val selectedKinds = binding.chipGroupKinds.checkedChipIds.mapNotNull { chipId ->
            when (chipId) {
                R.id.chipNature -> "natural"
                R.id.chipMuseums -> "museums"
                R.id.chipFood -> "foods"
                R.id.chipHistory -> "historic"
                R.id.chipArchitecture -> "architecture"
                R.id.chipCulture -> "cultural"
                else -> null
            }
        }

        return selectedKinds.joinToString(",").ifBlank { "interesting_places" }
    }

    private fun selectedOpenTripMapRate(): Int? {
        return when (binding.chipGroupRate.checkedChipId) {
            R.id.chipRateAny -> null
            R.id.chipRateNamed -> 2
            R.id.chipRatePopular -> 3
            else -> 2
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
