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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.example.smarttripplanner.databinding.QuestionnaireLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionnaireFragment : Fragment() {

    private var _binding: QuestionnaireLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuestionnaireViewModel by viewModels()
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
            submitQuestionnaire()
        }

        observeQuestionnaireState()
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

    private fun submitQuestionnaire() {
        val lat = userLatitude
        val lon = userLongitude

        if (lat == null || lon == null) {
            Toast.makeText(requireContext(), "Please secure your current location first", Toast.LENGTH_SHORT).show()
            return
        }

        val tripName = binding.etTripName.text?.toString()?.trim().orEmpty()
        val tripDate = binding.etTripDate.text?.toString()?.trim().orEmpty()
        val participants = binding.etParticipants.text?.toString()?.toIntOrNull()
        val startTime = binding.etStartTime.text?.toString()?.trim().orEmpty()
        val endTime = binding.etEndTime.text?.toString()?.trim().orEmpty()

        if (tripName.isBlank()) {
            Toast.makeText(requireContext(), "Please enter a trip name", Toast.LENGTH_SHORT).show()
            return
        }

        if (tripDate.isBlank()) {
            Toast.makeText(requireContext(), "Please enter a trip date", Toast.LENGTH_SHORT).show()
            return
        }

        if (participants == null || participants <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid participants count", Toast.LENGTH_SHORT).show()
            return
        }

        val radiusKilometers = binding.sliderMaxDistance.value.toInt()
        val selectedKindChipIds = binding.chipGroupKinds.checkedChipIds

        viewModel.generateTrip(
            name = tripName,
            date = tripDate,
            lat = lat,
            lon = lon,
            radiusKilometers = radiusKilometers,
            selectedKindChipIds = selectedKindChipIds,
            participantsCount = participants,
            startTime = startTime,
            endTime = endTime
        )
    }

    private fun observeQuestionnaireState() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                QuestionnaireUiState.Idle -> {
                    binding.btnFindPlaces.isEnabled = true
                    binding.btnFindPlaces.text = "Find places"
                }
                QuestionnaireUiState.Loading -> {
                    binding.btnFindPlaces.isEnabled = false
                    binding.btnFindPlaces.text = "Creating trip..."
                }
                is QuestionnaireUiState.Success -> {
                    binding.btnFindPlaces.isEnabled = true
                    binding.btnFindPlaces.text = "Find places"
                    val action =
                        QuestionnaireFragmentDirections.actionQuestionnaireFragmentToTripDetailsFragment(state.tripId)
                    findNavController().navigate(action)
                    viewModel.resetState()
                }
                is QuestionnaireUiState.Error -> {
                    binding.btnFindPlaces.isEnabled = true
                    binding.btnFindPlaces.text = "Find places"
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
