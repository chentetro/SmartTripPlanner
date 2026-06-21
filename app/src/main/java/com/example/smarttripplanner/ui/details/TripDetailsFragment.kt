package com.example.smarttripplanner.ui.details

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smarttripplanner.R
import com.example.smarttripplanner.data.model.Trip
import com.example.smarttripplanner.databinding.TripDetailsBinding
import com.example.smarttripplanner.ui.adapters.SiteAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TripDetailsFragment : Fragment() {

    private var _binding: TripDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripDetailsViewModel by viewModels()
    private lateinit var siteAdapter: SiteAdapter
    private var currentTripId: Long = -1L
    private var isFavorite: Boolean = false
    private var currentTrip: Trip? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TripDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentTripId = arguments?.getLong("tripId", -1L) ?: -1L

        if (currentTripId == -1L) return

        siteAdapter = SiteAdapter(
            onSiteClick = { placeId ->
                findNavController().navigate(
                    R.id.action_tripDetailsFragment_to_siteDetailsFragment,
                    bundleOf("placeId" to placeId)
                )
            },
            onDeleteClick = { site ->
                viewModel.deleteSite(site.siteId)
            }
        )

        binding.rvTripSites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = siteAdapter
        }

        val dateFormatter = SimpleDateFormat(getString(R.string.trip_date_format), Locale.getDefault())

        viewModel.setTripId(currentTripId)

        viewModel.trip.observe(viewLifecycleOwner) { trip ->
            if (trip == null) return@observe

            currentTrip = trip
            isFavorite = trip.isFavorite
            binding.tvDetailTripName.text = trip.tripName
            binding.tvDetailTripDate.text = dateFormatter.format(Date(trip.tripDate))
            binding.tvDetailTripTimeRange.text = getString(
                R.string.trip_time_range_format,
                trip.totalStartTime,
                trip.totalEndTime
            )
            binding.tvDetailTripMeta.text = getString(
                R.string.trip_details_meta_format,
                trip.vibe,
                trip.participantsCount,
                trip.maxDistance.toInt()
            )

            updateFavoriteButton(isFavorite)
        }

        binding.btnAddToFavorites.setOnClickListener {
            viewModel.toggleFavoriteStatus(currentTripId, isFavorite)
        }

        binding.btnEditTripSettings.setOnClickListener {
            currentTrip?.let(::showEditTripDialog)
        }

        viewModel.savedSites.observe(viewLifecycleOwner) { sites ->
            siteAdapter.submitList(sites)
        }

        viewModel.editState.observe(viewLifecycleOwner) { state ->
            when (state) {
                TripDetailsEditState.Idle -> Unit
                TripDetailsEditState.Saved -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.trip_details_updated),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.resetEditState()
                }
                is TripDetailsEditState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetEditState()
                }
            }
        }
    }

    private fun showEditTripDialog(trip: Trip) {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24.dpToPx(), 8.dpToPx(), 24.dpToPx(), 0)
        }

        val nameInput = createDialogEditText(getString(R.string.trip_name), trip.tripName)
        val startTimeInput = createDialogEditText(getString(R.string.start_time), trip.totalStartTime)
        val endTimeInput = createDialogEditText(getString(R.string.end_time), trip.totalEndTime)

        container.addView(nameInput)
        container.addView(startTimeInput)
        container.addView(endTimeInput)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.edit_trip_details))
            .setView(container)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                viewModel.updateTripDetails(
                    tripId = currentTripId,
                    name = nameInput.text?.toString().orEmpty(),
                    startTime = startTimeInput.text?.toString().orEmpty(),
                    endTime = endTimeInput.text?.toString().orEmpty()
                )
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun createDialogEditText(hint: String, value: String): EditText {
        return EditText(requireContext()).apply {
            this.hint = hint
            setText(value)
            inputType = InputType.TYPE_CLASS_TEXT
            setSingleLine(true)
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.btnAddToFavorites.text =
            if (isFavorite) {
                getString(R.string.remove_from_favorites)
            } else {
                getString(R.string.add_to_favorites)
            }
    }
    //להגדיר כאן שהADAPTER שלו זה SITEADAPTER

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
