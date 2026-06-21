package com.example.smarttripplanner.ui.questionnaire

import android.Manifest
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
import com.example.smarttripplanner.R
import com.example.smarttripplanner.databinding.QuestionnaireLayoutBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionnaireFragment : Fragment() {

    private var _binding: QuestionnaireLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuestionnaireViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (isGranted) {
                fetchCurrentLocation()
            } else {
                showLocationPermissionDenied()
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
        binding.tvRadiusValue.text = getString(R.string.questionnaire_radius_value, distanceKm)
    }

    private fun requestLocationOrFetch() {
        if (hasLocationPermission()) {
            fetchCurrentLocation()
        } else {
            binding.tvLocationStatus.text = getString(R.string.questionnaire_location_permission_pending)
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun hasFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasLocationPermission(): Boolean {
        return hasFineLocationPermission() || ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun fetchCurrentLocation() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted && !coarseLocationGranted) {
            showLocationPermissionDenied()
            return
        }

        binding.tvLocationStatus.text = getString(R.string.questionnaire_getting_location)
        val priority = if (fineLocationGranted) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

        fusedLocationClient.getCurrentLocation(priority, null)
            .addOnSuccessListener { location ->
                val currentBinding = _binding ?: return@addOnSuccessListener
                if (location == null) {
                    currentBinding.tvLocationStatus.text =
                        getString(R.string.questionnaire_location_unavailable)
                    return@addOnSuccessListener
                }

                viewModel.updateLocation(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
                currentBinding.tvLocationStatus.text = getString(R.string.questionnaire_location_secured)
            }
            .addOnFailureListener {
                val currentBinding = _binding ?: return@addOnFailureListener
                currentBinding.tvLocationStatus.text =
                    getString(R.string.questionnaire_location_unavailable)
            }
    }

    private fun showLocationPermissionDenied() {
        val currentBinding = _binding ?: return
        currentBinding.tvLocationStatus.text =
            getString(R.string.questionnaire_location_permission_denied_status)
        context?.let {
            Toast.makeText(
                it,
                getString(R.string.questionnaire_location_permission_denied_message),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun submitQuestionnaire() {
        viewModel.generateTrip(
            name = binding.etTripName.text?.toString().orEmpty(),
            date = binding.etTripDate.text?.toString().orEmpty(),
            participantsText = binding.etParticipants.text?.toString().orEmpty(),
            startTime = binding.etStartTime.text?.toString().orEmpty(),
            endTime = binding.etEndTime.text?.toString().orEmpty(),
            radiusKilometers = binding.sliderMaxDistance.value.toInt(),
            selectedKindChipIds = binding.chipGroupKinds.checkedChipIds
        )
    }

    private fun observeQuestionnaireState() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                QuestionnaireUiState.Idle -> {
                    binding.btnFindPlaces.isEnabled = true
                    binding.btnFindPlaces.text = getString(R.string.questionnaire_find_places)
                }
                QuestionnaireUiState.Loading -> {
                    binding.btnFindPlaces.isEnabled = false
                    binding.btnFindPlaces.text = getString(R.string.questionnaire_creating_trip)
                }
                is QuestionnaireUiState.Success -> {
                    binding.btnFindPlaces.isEnabled = true
                    binding.btnFindPlaces.text = getString(R.string.questionnaire_find_places)
                    val action =
                        QuestionnaireFragmentDirections.actionQuestionnaireFragmentToTripDetailsFragment(state.tripId)
                    findNavController().navigate(action)
                    viewModel.resetState()
                }
                is QuestionnaireUiState.Error -> {
                    binding.btnFindPlaces.isEnabled = true
                    binding.btnFindPlaces.text = getString(R.string.questionnaire_find_places)
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
